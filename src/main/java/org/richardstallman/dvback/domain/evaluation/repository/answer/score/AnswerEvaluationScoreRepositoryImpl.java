package org.richardstallman.dvback.domain.evaluation.repository.answer.score;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationScoreConverter;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationScoreDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnswerEvaluationScoreRepositoryImpl implements AnswerEvaluationScoreRepository {

  private final AnswerEvaluationScoreConverter answerEvaluationScoreConverter;
  private final AnswerEvaluationScoreJpaRepository answerEvaluationScoreJpaRepository;

  @Override
  public AnswerEvaluationScoreDomain save(AnswerEvaluationScoreDomain answerEvaluationScoreDomain) {
    return answerEvaluationScoreConverter.fromEntityToDomain(
        answerEvaluationScoreJpaRepository.save(
            answerEvaluationScoreConverter.fromDomainToEntity(answerEvaluationScoreDomain)));
  }

  @Override
  public List<AnswerEvaluationScoreDomain> saveAll(
      List<AnswerEvaluationScoreDomain> answerEvaluationScoreDomains) {
    return answerEvaluationScoreJpaRepository
        .saveAll(
            answerEvaluationScoreDomains.stream()
                .map(answerEvaluationScoreConverter::fromDomainToEntity)
                .toList())
        .stream()
        .map(answerEvaluationScoreConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<AnswerEvaluationScoreDomain> findByAnswerEvaluationId(Long answerEvaluationId) {
    return answerEvaluationScoreJpaRepository
        .findByAnswerEvaluationEntityAnswerEvaluationId(answerEvaluationId)
        .stream()
        .map(answerEvaluationScoreConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<AnswerEvaluationScoreDomain> findByQuestionId(Long questionId) {
    return answerEvaluationScoreJpaRepository
        .findByAnswerEvaluationEntityQuestionQuestionId(questionId)
        .stream()
        .map(answerEvaluationScoreConverter::fromEntityToDomain)
        .toList();
  }
}
