package org.richardstallman.dvback.domain.file.service;

import java.util.List;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;

public interface CoverLetterService {

  CoverLetterResponseDto createCoverLetter(
      CoverLetterRequestDto coverLetterRequestDto, Long userId);

  CoverLetterDomain createCoverLetter(CoverLetterDomain coverLetterDomain);

  CoverLetterDomain findCoverLetter(Long coverLetterId);

  CoverLetterDomain findCoverLetterByInterviewId(Long interviewId);

  List<CoverLetterResponseDto> findCoverLettersByUserId(Long userId);
}
