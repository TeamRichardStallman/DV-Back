package org.richardstallman.dvback.domain.evaluation.converter;

import org.richardstallman.dvback.domain.evaluation.domain.EvaluationCriteriaDomain;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.evaluation.entity.EvaluationCriteriaEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EvaluationCriteriaConverter {

  private final OverallEvaluationConverter overallEvaluationConverter;

  public EvaluationCriteriaConverter(@Lazy OverallEvaluationConverter overallEvaluationConverter) {
    this.overallEvaluationConverter = overallEvaluationConverter;
  }

  public EvaluationCriteriaEntity fromDomainToEntity(
      EvaluationCriteriaDomain evaluationCriteriaDomain) {
    return new EvaluationCriteriaEntity(
        evaluationCriteriaDomain.getEvaluationCriteriaId(),
        evaluationCriteriaDomain.getEvaluationCriteria(),
        evaluationCriteriaDomain.getFeedbackText(),
        evaluationCriteriaDomain.getScore(),
        overallEvaluationConverter.fromDomainToEntity(
            evaluationCriteriaDomain.getOverallEvaluationDomain()));
  }

  public EvaluationCriteriaDomain fromEntityToDomain(
      EvaluationCriteriaEntity evaluationCriteriaEntity) {
    return EvaluationCriteriaDomain.builder()
        .evaluationCriteriaId(evaluationCriteriaEntity.getEvaluationCriteriaId())
        .evaluationCriteria(evaluationCriteriaEntity.getEvaluationCriteriaName())
        .feedbackText(evaluationCriteriaEntity.getFeedbackText())
        .score(evaluationCriteriaEntity.getScore())
        .build();
  }

  public EvaluationCriteriaResponseDto fromDomainToResponseDto(
      EvaluationCriteriaDomain evaluationCriteriaDomain) {
    return new EvaluationCriteriaResponseDto(
        evaluationCriteriaDomain.getEvaluationCriteriaId(),
        evaluationCriteriaDomain.getEvaluationCriteria().name(),
        evaluationCriteriaDomain.getFeedbackText(),
        evaluationCriteriaDomain.getScore());
  }
}
