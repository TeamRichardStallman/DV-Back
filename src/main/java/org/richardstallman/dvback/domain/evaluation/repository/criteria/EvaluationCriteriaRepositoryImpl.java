package org.richardstallman.dvback.domain.evaluation.repository.criteria;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.evaluation.converter.EvaluationCriteriaConverter;
import org.richardstallman.dvback.domain.evaluation.domain.EvaluationCriteriaDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationCriteriaRepositoryImpl implements EvaluationCriteriaRepository {

  private final EvaluationCriteriaConverter evaluationCriteriaConverter;
  private final EvaluationCriteriaJpaRepository evaluationCriteriaJpaRepository;

  @Override
  public EvaluationCriteriaDomain save(EvaluationCriteriaDomain evaluationCriteriaDomain) {
    return evaluationCriteriaConverter.fromEntityToDomain(
        evaluationCriteriaJpaRepository.save(
            evaluationCriteriaConverter.fromDomainToEntity(evaluationCriteriaDomain)));
  }

  @Override
  public List<EvaluationCriteriaDomain> saveAll(
      List<EvaluationCriteriaDomain> evaluationCriteriaDomains) {
    return evaluationCriteriaJpaRepository
        .saveAll(
            evaluationCriteriaDomains.stream()
                .map(evaluationCriteriaConverter::fromDomainToEntity)
                .toList())
        .stream()
        .map(evaluationCriteriaConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<EvaluationCriteriaDomain> findByOverallEvaluationId(Long overallEvaluationId) {
    return evaluationCriteriaJpaRepository
        .findByOverallEvaluationOverallEvaluationId(overallEvaluationId)
        .stream()
        .map(evaluationCriteriaConverter::fromEntityToDomain)
        .toList();
  }
}
