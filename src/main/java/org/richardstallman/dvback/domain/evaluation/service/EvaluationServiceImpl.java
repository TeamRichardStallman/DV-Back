package org.richardstallman.dvback.domain.evaluation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.firebase.service.FirebaseMessagingService;
import org.richardstallman.dvback.client.python.PythonService;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.constant.CommonConstants.EvaluationCriteria;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationScoreConverter;
import org.richardstallman.dvback.domain.evaluation.converter.EvaluationCriteriaConverter;
import org.richardstallman.dvback.domain.evaluation.converter.OverallEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.EvaluationCriteriaDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.personal.EvaluationExternalPersonalAnswerDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.personal.EvaluationExternalPersonalAnswerRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.personal.EvaluationExternalPersonalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.technical.EvaluationExternalTechnicalAnswerDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.technical.EvaluationExternalTechnicalAnswerRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.technical.EvaluationExternalTechnicalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultCriteriaDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.evaluation.repository.answer.AnswerEvaluationRepository;
import org.richardstallman.dvback.domain.evaluation.repository.answer.score.AnswerEvaluationScoreRepository;
import org.richardstallman.dvback.domain.evaluation.repository.criteria.EvaluationCriteriaRepository;
import org.richardstallman.dvback.domain.evaluation.repository.overall.OverallEvaluationRepository;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

  private final OverallEvaluationRepository overallEvaluationRepository;
  private final OverallEvaluationConverter overallEvaluationConverter;
  private final AnswerRepository answerRepository;
  private final QuestionRepository questionRepository;
  private final PythonService pythonService;
  private final AnswerEvaluationConverter answerEvaluationConverter;
  private final EvaluationCriteriaRepository evaluationCriteriaRepository;
  private final AnswerEvaluationRepository answerEvaluationRepository;
  private final EvaluationCriteriaConverter evaluationCriteriaConverter;
  private final AnswerEvaluationScoreRepository answerEvaluationScoreRepository;
  private final AnswerEvaluationScoreConverter answerEvaluationScoreConverter;
  private final CoverLetterConverter coverLetterConverter;
  private final InterviewService interviewService;
  private final QuestionConverter questionConverter;
  private final InterviewConverter interviewConverter;
  private final FirebaseMessagingService firebaseMessagingService;
  private final S3Service s3Service;

  @Override
  public void getOverallEvaluation(
      OverallEvaluationRequestDto overallEvaluationRequestDto, Long userId) {
    List<QuestionDomain> questions = retrieveQuestions(overallEvaluationRequestDto.interviewId());
    InterviewDomain interviewDomain = questions.get(0).getInterviewDomain();

    List<String> filePaths = new ArrayList<>();
    if (interviewDomain.getInterviewMode() == InterviewMode.REAL) {
      filePaths.add(interviewDomain.getCoverLetter().getS3FileUrl());
    }
    callPythonEvaluationService(userId, questions, filePaths);
    log.info("Request Evaluation To Python: Success");
  }

  @Override
  public OverallEvaluationResponseDto getOverallEvaluationByInterviewId(Long interviewId) {
    InterviewDomain interviewDomain = interviewService.getInterviewById(interviewId);
    OverallEvaluationDomain overallEvaluationDomain =
        OverallEvaluationDomain.builder()
            .overallEvaluationId(
                getOverallEvaluationDomainByInterviewId(interviewId).getOverallEvaluationId())
            .interviewDomain(interviewDomain)
            .build();
    List<AnswerEvaluationDomain> answerEvaluationDomains =
        answerEvaluationRepository.findByInterviewId(interviewId);
    return buildResponseDto(interviewDomain, overallEvaluationDomain, answerEvaluationDomains);
  }

  @Override
  public OverallEvaluationDomain getOverallEvaluationDomainById(Long overallEvaluationId) {
    return overallEvaluationRepository.findById(overallEvaluationId);
  }

  @Override
  public void saveCompletedEvaluation(OverallEvaluationResultRequestDto evaluationResult) {
    log.info("EvaluationServiceImpl:: save completed evaluation requested");
    Long userId = evaluationResult.userId();
    Long interviewId = evaluationResult.interviewId();
    OverallEvaluationResultDto overallEvaluationResultDto = evaluationResult.overallEvaluation();

    OverallEvaluationDomain overallEvaluationDomain =
        getOverallEvaluationDomainByInterviewId(interviewId);
    saveEvaluationCriteriaResult(overallEvaluationDomain, overallEvaluationResultDto);

    firebaseMessagingService.sendNotification(
        userId, "평가가 완료되었습니다.", evaluationResult.interviewId().toString());
    log.info("EvaluationServiceImpl:: save completed evaluation succeed");
  }

  private void saveEvaluationCriteriaResult(
      OverallEvaluationDomain overallEvaluationDomain,
      OverallEvaluationResultDto overallEvaluationResultDto) {
    Map<EvaluationCriteria, OverallEvaluationResultCriteriaDto> criteriaMap = new HashMap<>();
    if (overallEvaluationDomain.getInterviewDomain().getInterviewType()
        == InterviewType.TECHNICAL) {
      criteriaMap.put(
          EvaluationCriteria.JOB_FIT, overallEvaluationResultDto.textOverall().jobFit());
      criteriaMap.put(
          EvaluationCriteria.GROWTH_POTENTIAL,
          overallEvaluationResultDto.textOverall().growthPotential());
      criteriaMap.put(
          EvaluationCriteria.WORK_ATTITUDE,
          overallEvaluationResultDto.textOverall().workAttitude());
      criteriaMap.put(
          EvaluationCriteria.TECHNICAL_DEPTH,
          overallEvaluationResultDto.textOverall().technicalDepth());

      if (overallEvaluationDomain.getInterviewDomain().getInterviewMethod()
          == InterviewMethod.VOICE) {
        criteriaMap.put(
            EvaluationCriteria.FLUENCY, overallEvaluationResultDto.voiceOverall().fluency());
        criteriaMap.put(
            EvaluationCriteria.CLARITY, overallEvaluationResultDto.voiceOverall().clarity());
        criteriaMap.put(
            EvaluationCriteria.WORD_REPETITION,
            overallEvaluationResultDto.voiceOverall().wordRepetition());
      }
    }

    if (overallEvaluationDomain.getInterviewDomain().getInterviewType() == InterviewType.PERSONAL) {
      criteriaMap.put(
          EvaluationCriteria.COMPANY_FIT, overallEvaluationResultDto.textOverall().companyFit());
      criteriaMap.put(
          EvaluationCriteria.ADAPTABILITY, overallEvaluationResultDto.textOverall().adaptability());
      criteriaMap.put(
          EvaluationCriteria.INTERPERSONAL_SKILLS,
          overallEvaluationResultDto.textOverall().interpersonalSkills());
      criteriaMap.put(
          EvaluationCriteria.GROWTH_ATTITUDE,
          overallEvaluationResultDto.textOverall().growthAttitude());

      if (overallEvaluationDomain.getInterviewDomain().getInterviewMethod()
          == InterviewMethod.VOICE) {
        criteriaMap.put(
            EvaluationCriteria.FLUENCY, overallEvaluationResultDto.voiceOverall().fluency());
        criteriaMap.put(
            EvaluationCriteria.CLARITY, overallEvaluationResultDto.voiceOverall().clarity());
        criteriaMap.put(
            EvaluationCriteria.WORD_REPETITION,
            overallEvaluationResultDto.voiceOverall().wordRepetition());
      }
    }

    List<EvaluationCriteriaDomain> criteriaDomains =
        criteriaMap.entrySet().stream()
            .map(
                entry ->
                    EvaluationCriteriaDomain.builder()
                        .evaluationCriteria(entry.getKey())
                        .overallEvaluationDomain(overallEvaluationDomain)
                        .feedbackText(entry.getValue().rationale())
                        .score(entry.getValue().score())
                        .build())
            .toList();
    evaluationCriteriaRepository.saveAll(criteriaDomains);
  }

  private OverallEvaluationDomain getOverallEvaluationDomainByInterviewId(Long interviewId) {
    return overallEvaluationRepository.findByInterviewId(interviewId);
  }

  private List<QuestionDomain> retrieveQuestions(Long interviewId) {
    return questionRepository.findQuestionsByInterviewId(interviewId);
  }

  private void callPythonEvaluationService(
      Long userId, List<QuestionDomain> questions, List<String> filePaths) {
    InterviewDomain interviewDomain = questions.get(0).getInterviewDomain();
    List<EvaluationExternalQuestionRequestDto> questionTexts =
        questions.stream()
            .map(questionConverter::fromDomainToEvaluationExternalRequestDto)
            .toList();

    List<AnswerDomain> answerDomains =
        questions.stream()
            .map(question -> answerRepository.findByQuestionId(question.getQuestionId()))
            .toList();

    if (interviewDomain.getInterviewType() == InterviewType.PERSONAL) {
      Map<AnswerDomain, EvaluationExternalPersonalAnswerDto> answerPersonalMap =
          answerDomains.stream()
              .collect(
                  Collectors.toMap(
                      answer -> answer,
                      answer ->
                          answerEvaluationConverter.fromDomainToExternalPersonalDto(
                              answer,
                              answerEvaluationRepository.findByQuestionId(
                                  answer.getQuestionDomain().getQuestionId()),
                              answerEvaluationScoreRepository.findByQuestionId(
                                  answer.getQuestionDomain().getQuestionId()))));

      List<EvaluationExternalPersonalAnswerRequestDto> answerPersonalRequestDtos =
          answerDomains.stream()
              .map(
                  e ->
                      new EvaluationExternalPersonalAnswerRequestDto(
                          e.getQuestionDomain().getQuestionId(), answerPersonalMap.get(e)))
              .toList();

      EvaluationExternalPersonalRequestDto personalRequestDto =
          new EvaluationExternalPersonalRequestDto(
              userId,
              interviewDomain.getInterviewMode(),
              interviewDomain.getInterviewType(),
              interviewDomain.getInterviewMethod(),
              interviewDomain.getJob().getJobName(),
              questionTexts,
              answerPersonalRequestDtos,
              filePaths);
      log.info(
          "start request personal overall evaluations to python with ({})", personalRequestDto);
      pythonService.getPersonalOverallEvaluations(
          personalRequestDto, interviewDomain.getInterviewId());
    } else if (interviewDomain.getInterviewType() == InterviewType.TECHNICAL) {
      Map<AnswerDomain, EvaluationExternalTechnicalAnswerDto> answerTechnicalMap =
          answerDomains.stream()
              .collect(
                  Collectors.toMap(
                      answer -> answer,
                      answer ->
                          answerEvaluationConverter.fromDomainToExternalTechnicalDto(
                              answer,
                              answerEvaluationRepository.findByQuestionId(
                                  answer.getQuestionDomain().getQuestionId()),
                              answerEvaluationScoreRepository.findByQuestionId(
                                  answer.getQuestionDomain().getQuestionId()))));

      List<EvaluationExternalTechnicalAnswerRequestDto> answerTechnicalRequestDtos =
          answerDomains.stream()
              .map(
                  e ->
                      new EvaluationExternalTechnicalAnswerRequestDto(
                          e.getQuestionDomain().getQuestionId(), answerTechnicalMap.get(e)))
              .toList();

      EvaluationExternalTechnicalRequestDto technicalRequestDto =
          new EvaluationExternalTechnicalRequestDto(
              userId,
              interviewDomain.getInterviewMode(),
              interviewDomain.getInterviewType(),
              interviewDomain.getInterviewMethod(),
              interviewDomain.getJob().getJobName(),
              questionTexts,
              answerTechnicalRequestDtos,
              filePaths);
      log.info(
          "start request technical overall evaluations to python with ({})", technicalRequestDto);
      pythonService.getTechnicalOverallEvaluations(
          technicalRequestDto, interviewDomain.getInterviewId());
    }
  }

  private OverallEvaluationResponseDto buildResponseDto(
      InterviewDomain interviewDomain,
      OverallEvaluationDomain overallEvaluation,
      List<AnswerEvaluationDomain> answerEvaluations) {

    List<EvaluationCriteriaResponseDto> criteriaResponseDtos =
        evaluationCriteriaRepository
            .findByOverallEvaluationId(overallEvaluation.getOverallEvaluationId())
            .stream()
            .map(evaluationCriteriaConverter::fromDomainToResponseDto)
            .toList();

    boolean isVoice = interviewDomain.getInterviewMethod() == InterviewMethod.VOICE;

    List<AnswerEvaluationResponseDto> answerEvaluationResponseDtos =
        answerEvaluations.stream()
            .map(
                e ->
                    answerEvaluationConverter.fromDomainToResponseDto(
                        e,
                        answerRepository
                            .findByQuestionId(e.getQuestionDomain().getQuestionId())
                            .getAnswerText(),
                        isVoice
                            ? s3Service
                                .getDownloadURLForAudio(
                                    answerRepository
                                        .findByQuestionId(e.getQuestionDomain().getQuestionId())
                                        .getS3AudioUrl(),
                                    interviewDomain.getUserDomain().getUserId(),
                                    interviewDomain.getInterviewId(),
                                    e.getQuestionDomain().getQuestionId())
                                .preSignedUrl()
                            : null,
                        answerEvaluationScoreRepository
                            .findByAnswerEvaluationId(e.getAnswerEvaluationId())
                            .stream()
                            .map(answerEvaluationScoreConverter::fromDomainToResponseDto)
                            .toList()))
            .toList();

    List<FileResponseDto> fileResponseDtos = new ArrayList<>();

    if (interviewDomain.getInterviewMode() == InterviewMode.REAL) {
      fileResponseDtos.add(
          coverLetterConverter.fromDomainToResponseDto(interviewDomain.getCoverLetter()));
    }

    InterviewResponseDto interviewResponseDto =
        interviewConverter.fromDomainToResponseDto(interviewDomain, fileResponseDtos);

    return overallEvaluationConverter.toResponseDto(
        interviewResponseDto, criteriaResponseDtos, answerEvaluationResponseDtos);
  }
}
