package org.richardstallman.dvback.domain.interview.service;

import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewAddFileRequestDto;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewAddFileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationListResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewListResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;

public interface InterviewService {

  InterviewCreateResponseDto createInterview(
      InterviewCreateRequestDto interviewCreateRequestDto, Long userId);

  InterviewAddFileResponseDto addInterviewFile(
      InterviewAddFileRequestDto interviewAddFileRequestDto);

  InterviewListResponseDto getInterviewsByUserId(Long userId);

  InterviewDomain getInterviewById(Long interviewId);

  InterviewEvaluationListResponseDto getInterviewsByUserIdForEvaluation(Long userId);

  InterviewResponseDto getInterviewResponseDtoByDomain(InterviewDomain interviewDomain);

  boolean checkInterviewOwner(Long userId, Long interviewId);
}
