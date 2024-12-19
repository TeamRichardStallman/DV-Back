package org.richardstallman.dvback.domain.evaluation.converter;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationUserInfoResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.evaluation.entity.overall.OverallEvaluationEntity;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OverallEvaluationConverter {

  private final InterviewConverter interviewConverter;

  public OverallEvaluationConverter(@Lazy InterviewConverter interviewConverter) {
    this.interviewConverter = interviewConverter;
  }

  public OverallEvaluationEntity fromDomainToEntity(
      OverallEvaluationDomain overallEvaluationDomain) {
    return new OverallEvaluationEntity(
        overallEvaluationDomain.getOverallEvaluationId(),
        interviewConverter.fromDomainToEntity(overallEvaluationDomain.getInterviewDomain()));
  }

  public OverallEvaluationDomain fromEntityToDomain(
      OverallEvaluationEntity overallEvaluationEntity) {
    return OverallEvaluationDomain.builder()
        .overallEvaluationId(overallEvaluationEntity.getOverallEvaluationId())
        .interviewDomain(
            interviewConverter.fromEntityToDomain(overallEvaluationEntity.getInterview()))
        .build();
  }

  public OverallEvaluationResponseDto toResponseDto(
      InterviewResponseDto interviewResponseDto,
      List<EvaluationCriteriaResponseDto> evaluationCriteriaResponseDtos,
      List<AnswerEvaluationResponseDto> answerEvaluationResponseDtos) {
    return new OverallEvaluationResponseDto(
        interviewResponseDto, evaluationCriteriaResponseDtos, answerEvaluationResponseDtos);
  }

  public OverallEvaluationUserInfoResponseDto toUserInfoResponseDto(
      InterviewEvaluationResponseDto interviewEvaluationResponseDto) {
    return new OverallEvaluationUserInfoResponseDto(
        interviewEvaluationResponseDto.interviewTitle(),
        interviewEvaluationResponseDto.interviewId());
  }
}
