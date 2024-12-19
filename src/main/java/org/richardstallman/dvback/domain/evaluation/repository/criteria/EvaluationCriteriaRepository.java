package org.richardstallman.dvback.domain.evaluation.repository.criteria;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.EvaluationCriteriaDomain;

public interface EvaluationCriteriaRepository {

  EvaluationCriteriaDomain save(EvaluationCriteriaDomain evaluationCriteriaDomain);

  List<EvaluationCriteriaDomain> saveAll(List<EvaluationCriteriaDomain> evaluationCriteriaDomains);

  List<EvaluationCriteriaDomain> findByOverallEvaluationId(Long overallEvaluationId);
}
