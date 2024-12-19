package org.richardstallman.dvback.domain.evaluation.converter;

import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationScoreDomain;
import org.richardstallman.dvback.domain.evaluation.domain.response.AnswerEvaluationScoreResponseDto;
import org.richardstallman.dvback.domain.evaluation.entity.answer.AnswerEvaluationScoreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class AnswerEvaluationScoreConverter {

  private final AnswerEvaluationConverter answerEvaluationConverter;

  @Autowired
  public AnswerEvaluationScoreConverter(@Lazy AnswerEvaluationConverter answerEvaluationConverter) {
    this.answerEvaluationConverter = answerEvaluationConverter;
  }

  public AnswerEvaluationScoreDomain fromEntityToDomain(
      AnswerEvaluationScoreEntity answerEvaluationScoreEntity) {
    return AnswerEvaluationScoreDomain.builder()
        .answerEvaluationScoreId(answerEvaluationScoreEntity.getAnswerEvaluationScoreId())
        .answerEvaluationScoreName(answerEvaluationScoreEntity.getAnswerEvaluationScoreName())
        .score(answerEvaluationScoreEntity.getScore())
        .rationale(answerEvaluationScoreEntity.getRationale())
        .answerEvaluationDomain(
            answerEvaluationConverter.fromEntityToDomain(
                answerEvaluationScoreEntity.getAnswerEvaluationEntity()))
        .answerEvaluationType(answerEvaluationScoreEntity.getAnswerEvaluationType())
        .build();
  }

  public AnswerEvaluationScoreEntity fromDomainToEntity(
      AnswerEvaluationScoreDomain answerEvaluationScoreDomain) {
    return new AnswerEvaluationScoreEntity(
        answerEvaluationScoreDomain.getAnswerEvaluationScoreId(),
        answerEvaluationScoreDomain.getAnswerEvaluationScoreName(),
        answerEvaluationScoreDomain.getScore(),
        answerEvaluationScoreDomain.getRationale(),
        answerEvaluationConverter.fromDomainToEntity(
            answerEvaluationScoreDomain.getAnswerEvaluationDomain()),
        answerEvaluationScoreDomain.getAnswerEvaluationType());
  }

  public AnswerEvaluationScoreResponseDto fromDomainToResponseDto(
      AnswerEvaluationScoreDomain answerEvaluationScoreDomain) {
    return new AnswerEvaluationScoreResponseDto(
        answerEvaluationScoreDomain.getAnswerEvaluationScoreId(),
        answerEvaluationScoreDomain.getAnswerEvaluationScoreName().name(),
        answerEvaluationScoreDomain.getScore(),
        answerEvaluationScoreDomain.getRationale(),
        answerEvaluationScoreDomain.getAnswerEvaluationType().name());
  }
}
