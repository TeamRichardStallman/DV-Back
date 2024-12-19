package org.richardstallman.dvback.domain.file.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;
import org.richardstallman.dvback.domain.file.repository.coverletter.CoverLetterRepository;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoverLetterServiceImpl implements CoverLetterService {

  private final CoverLetterRepository coverLetterRepository;
  private final CoverLetterConverter coverLetterConverter;
  private final InterviewService interviewService;
  private final FileService fileService;
  private final UserRepository userRepository;

  public CoverLetterServiceImpl(
      CoverLetterRepository coverLetterRepository,
      CoverLetterConverter coverLetterConverter,
      @Lazy InterviewService interviewService,
      FileService fileService,
      UserRepository userRepository) {
    this.coverLetterRepository = coverLetterRepository;
    this.coverLetterConverter = coverLetterConverter;
    this.interviewService = interviewService;
    this.fileService = fileService;
    this.userRepository = userRepository;
  }

  @Override
  public CoverLetterResponseDto createCoverLetter(
      CoverLetterRequestDto coverLetterRequestDto, Long userId) {
    String fileName = fileService.getFileName(coverLetterRequestDto.getFilePath());
    if (fileName == null) {
      String[] sp = coverLetterRequestDto.getFilePath().split("/");
      fileName = sp[sp.length - 1];
    }
    UserDomain userDomain =
        userRepository
            .findById(userId)
            .orElseThrow(
                (() ->
                    new ApiException(
                        HttpStatus.NOT_FOUND, String.format("(%d): User Not Found", userId))));
    CoverLetterDomain coverLetterDomain =
        coverLetterRepository.save(
            coverLetterConverter.fromRequestDtoToDomain(
                coverLetterRequestDto, userDomain, fileName));
    return new CoverLetterResponseDto(
        coverLetterDomain.getCoverLetterId(),
        coverLetterDomain.getFileName(),
        coverLetterDomain.getS3FileUrl());
  }

  @Override
  public CoverLetterDomain createCoverLetter(CoverLetterDomain coverLetterDomain) {
    return coverLetterRepository.save(coverLetterDomain);
  }

  @Override
  public CoverLetterDomain findCoverLetter(Long coverLetterId) {
    return coverLetterRepository.findByCoverLetterId(coverLetterId);
  }

  @Override
  public CoverLetterDomain findCoverLetterByInterviewId(Long interviewId) {
    return interviewService.getInterviewById(interviewId).getCoverLetter();
  }

  @Override
  public List<CoverLetterResponseDto> findCoverLettersByUserId(Long userId) {
    return coverLetterRepository.findCoverLetterListByUserId(userId).stream()
        .map(coverLetterConverter::fromDomainToResponseDto)
        .toList();
  }
}
