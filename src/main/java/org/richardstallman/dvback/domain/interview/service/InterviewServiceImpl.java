package org.richardstallman.dvback.domain.interview.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.repository.overall.OverallEvaluationRepository;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.file.service.CoverLetterService;
import org.richardstallman.dvback.domain.file.service.FileService;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewAddFileRequestDto;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewAddFileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationListResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewListResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketResponseDto;
import org.richardstallman.dvback.domain.ticket.service.TicketService;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

  private final InterviewRepository interviewRepository;
  private final JobService jobService;
  private final InterviewConverter interviewConverter;
  private final FileService fileService;
  private final CoverLetterConverter coverLetterConverter;
  private final CoverLetterService coverLetterService;
  private final UserService userService;
  private final TicketService ticketService;
  private final OverallEvaluationRepository overallEvaluationRepository;

  public static final String ERROR_INTERVIEW_ID_NOT_NULL =
      "Interview ID should be null for new interviews";
  public static final String ERROR_INTERVIEW_ID_NULL_AFTER_SAVE =
      "Interview ID was not generated properly";

  @Override
  @Transactional
  public InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto, Long userId) {

    JobDomain jobDomain = jobService.findJobById(interviewCreateRequestDto.jobId());

    TicketResponseDto ticketResponseDto =
        confirmTicket(interviewCreateRequestDto, userId, interviewCreateRequestDto.questionCount());

    InterviewDomain interviewDomain =
        initializeInterviewDomain(interviewCreateRequestDto, jobDomain, userId);
    validateInterviewIdNull(interviewDomain);

    InterviewDomain createdInterviewDomain = interviewRepository.save(interviewDomain);
    validateInterviewIdAssigned(createdInterviewDomain);

    List<FileResponseDto> fileResponseDtos = new ArrayList<>();

    return interviewConverter.fromDomainToCreateResponseDto(
        createdInterviewDomain, fileResponseDtos, ticketResponseDto);
  }

  @Override
  public InterviewAddFileResponseDto addInterviewFile(
      InterviewAddFileRequestDto interviewAddFileRequestDto) {
    InterviewDomain target = getInterviewById(interviewAddFileRequestDto.interviewId());

    String fileName =
        fileService.getFileName(getCoverLetterUrl(interviewAddFileRequestDto.coverLetter()));
    if (fileName == null) {
      String[] sp = getCoverLetterUrl(interviewAddFileRequestDto.coverLetter()).split("/");
      fileName = sp[sp.length - 1];
    }
    UserResponseDto userResponseDto = userService.getUserInfo(target.getUserDomain().getUserId());
    CoverLetterDomain coverLetterDomain =
        createCoverLetter(interviewAddFileRequestDto, fileName, userResponseDto);
    InterviewDomain addedInterviewFile =
        interviewConverter.addInterviewFile(target, coverLetterDomain);
    InterviewDomain updatedInterview = interviewRepository.reSave(addedInterviewFile);
    FileResponseDto fileResponseDto =
        coverLetterConverter.fromDomainToResponseDto(coverLetterDomain);
    return interviewConverter.fromDomainToAddFileResponseDto(updatedInterview, fileResponseDto);
  }

  private TicketResponseDto confirmTicket(
      InterviewCreateRequestDto interviewCreateRequestDto, Long userId, int questionCount) {
    validateUserTicket(interviewCreateRequestDto, userId, questionCount);
    TicketTransactionRequestDto ticketTransactionRequestDto =
        new TicketTransactionRequestDto(
            -1 * calculateRequiredTickets(questionCount),
            TicketTransactionType.USE,
            fromInterviewMethodToTicketTransactionMethod(
                interviewCreateRequestDto.interviewMethod()),
            interviewCreateRequestDto.interviewMode(),
            fromInterviewMethodToInterviewAssetType(interviewCreateRequestDto.interviewMethod()),
            "");
    return ticketService.useTicket(ticketTransactionRequestDto, userId);
  }

  private TicketTransactionMethod fromInterviewMethodToTicketTransactionMethod(
      InterviewMethod interviewMethod) {
    if (interviewMethod == InterviewMethod.CHAT) {
      return TicketTransactionMethod.CHAT;
    } else if (interviewMethod == InterviewMethod.VOICE) {
      return TicketTransactionMethod.VOICE;
    }
    throw new ApiException(
        HttpStatus.BAD_REQUEST, "There is no such interview method: (" + interviewMethod + ")");
  }

  private InterviewAssetType fromInterviewMethodToInterviewAssetType(
      InterviewMethod interviewMethod) {
    if (interviewMethod == InterviewMethod.CHAT) {
      return InterviewAssetType.CHAT;
    } else if (interviewMethod == InterviewMethod.VOICE) {
      return InterviewAssetType.VOICE;
    }
    throw new ApiException(
        HttpStatus.BAD_REQUEST, "There is no such interview method: (" + interviewMethod + ")");
  }

  private void validateUserTicket(
      InterviewCreateRequestDto interviewCreateRequestDto, Long userId, int questionCount) {

    InterviewMode interviewMode = interviewCreateRequestDto.interviewMode();
    InterviewMethod interviewMethod = interviewCreateRequestDto.interviewMethod();

    int availableTickets = getUserTicketCount(interviewMode, interviewMethod, userId);

    int requiredTickets = calculateRequiredTickets(questionCount);

    if (availableTickets < requiredTickets) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST,
          String.format(
              "%d user doesn't have enough %s %s balance",
              userId, interviewMode.getKoreanName(), interviewMethod.getKoreanName()));
    }
  }

  private int getUserTicketCount(InterviewMode mode, InterviewMethod method, Long userId) {
    if (mode == InterviewMode.REAL) {
      return switch (method) {
        case CHAT -> ticketService.getUserRealChatTicketCount(userId);
        case VOICE -> ticketService.getUserRealVoiceTicketCount(userId);
        default -> throw new ApiException(
            HttpStatus.BAD_REQUEST, "No such interview method: " + method);
      };
    } else if (mode == InterviewMode.GENERAL) {
      return switch (method) {
        case CHAT -> ticketService.getUserGeneralChatTicketCount(userId);
        case VOICE -> ticketService.getUserGeneralVoiceTicketCount(userId);
        default -> throw new ApiException(
            HttpStatus.BAD_REQUEST, "No such interview method: " + method);
      };
    } else {
      throw new ApiException(HttpStatus.BAD_REQUEST, "No such interview mode: " + mode);
    }
  }

  private int calculateRequiredTickets(int questionCount) {
    final int QUESTIONS_PER_TICKET = 5;
    return (int) Math.ceil((double) questionCount / QUESTIONS_PER_TICKET);
  }

  @Override
  public InterviewListResponseDto getInterviewsByUserId(Long userId) {
    List<InterviewResponseDto> interviewResponseDtos =
        interviewRepository.findInterviewsByUserId(userId).stream()
            .map(
                (e) ->
                    interviewConverter.fromDomainToResponseDto(
                        e, e.getInterviewMode() == InterviewMode.REAL ? getFiles(e) : null))
            .toList();
    return new InterviewListResponseDto(interviewResponseDtos);
  }

  private List<FileResponseDto> getFiles(InterviewDomain interviewDomain) {
    List<FileResponseDto> fileResponseDtos = new ArrayList<>();
    if (interviewDomain.getInterviewMode() == InterviewMode.REAL) {
      fileResponseDtos.add(
          coverLetterConverter.fromDomainToResponseDto(interviewDomain.getCoverLetter()));
    }
    return fileResponseDtos;
  }

  @Override
  public InterviewDomain getInterviewById(Long interviewId) {
    return interviewRepository.findById(interviewId);
  }

  @Override
  public InterviewEvaluationListResponseDto getInterviewsByUserIdForEvaluation(Long userId) {
    List<InterviewDomain> interviewDomains =
        interviewRepository.findInterviewsByUserIdWithEvaluation(userId);
    return new InterviewEvaluationListResponseDto(
        interviewDomains.stream()
            .map(interviewConverter::fromDomainToEvaluationResponseDto)
            .toList());
  }

  @Override
  public InterviewResponseDto getInterviewResponseDtoByDomain(InterviewDomain interviewDomain) {
    return interviewConverter.fromDomainToResponseDto(interviewDomain, getFiles(interviewDomain));
  }

  @Override
  public boolean checkInterviewOwner(Long userId, Long interviewId) {
    InterviewDomain interviewDomain = getInterviewById(interviewId);
    if (!Objects.equals(interviewDomain.getUserDomain().getUserId(), userId)) {
      return false;
    }
    OverallEvaluationDomain overallEvaluationDomain =
        overallEvaluationRepository.findByInterviewId(interviewId);
    return overallEvaluationDomain == null;
  }

  private InterviewDomain initializeInterviewDomain(
      InterviewCreateRequestDto interviewCreateRequestDto, JobDomain jobDomain, Long userId) {
    UserResponseDto userResponseDto = userService.getUserInfo(userId);
    return validateInterviewTitle(
        interviewConverter.fromDtoToDomainWithStatusInitial(
            interviewCreateRequestDto, jobDomain, userResponseDto));
  }

  private String generateTitle(
      String jobType, String interviewMode, String interviewType, String interviewMethod) {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    String dateTime = now.format(formatter);

    return dateTime
        + "_"
        + jobType
        + "_"
        + interviewMode
        + "_"
        + interviewType
        + "_"
        + interviewMethod;
  }

  private InterviewDomain validateInterviewTitle(InterviewDomain interviewDomain) {
    if (interviewDomain.getInterviewTitle() == null
        || interviewDomain.getInterviewTitle().isEmpty()) {
      return InterviewDomain.builder()
          .interviewId(interviewDomain.getInterviewId())
          .userDomain(interviewDomain.getUserDomain())
          .interviewTitle(
              generateTitle(
                  interviewDomain.getJob().getJobNameKorean(),
                  interviewDomain.getInterviewMode().getKoreanName(),
                  interviewDomain.getInterviewType().getKoreanName(),
                  interviewDomain.getInterviewMethod().getKoreanName()))
          .interviewStatus(interviewDomain.getInterviewStatus())
          .interviewType(interviewDomain.getInterviewType())
          .interviewMethod(interviewDomain.getInterviewMethod())
          .interviewMode(interviewDomain.getInterviewMode())
          .job(interviewDomain.getJob())
          .coverLetter(interviewDomain.getCoverLetter())
          .build();
    }
    return interviewDomain;
  }

  private String getCoverLetterUrl(FileRequestDto file) {
    CoverLetterRequestDto coverLetterRequestDto =
        coverLetterConverter.fromFileRequestDtoToRequestDto(file);
    return coverLetterRequestDto.getFilePath();
  }

  private CoverLetterDomain createCoverLetter(
      InterviewAddFileRequestDto requestDto, String fileName, UserResponseDto userResponseDto) {
    return coverLetterService.createCoverLetter(
        coverLetterConverter.fromUrlToDomain(
            getCoverLetterUrl(requestDto.coverLetter()), userResponseDto, fileName));
  }

  private void validateInterviewIdNull(InterviewDomain interviewDomain) {
    if (interviewDomain.getInterviewId() != null) {
      log.error(ERROR_INTERVIEW_ID_NOT_NULL);
      throw new ApiException(HttpStatus.NOT_FOUND, ERROR_INTERVIEW_ID_NOT_NULL);
    }
  }

  private void validateInterviewIdAssigned(InterviewDomain interviewDomain) {
    if (interviewDomain.getInterviewId() == null) {
      log.error(ERROR_INTERVIEW_ID_NULL_AFTER_SAVE);
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERVIEW_ID_NULL_AFTER_SAVE);
    }
  }
}
