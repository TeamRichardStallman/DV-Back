package org.richardstallman.dvback.domain.evaluation.repository.criteria;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.entity.EvaluationCriteriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationCriteriaJpaRepository
    extends JpaRepository<EvaluationCriteriaEntity, Long> {

  List<EvaluationCriteriaEntity> findByOverallEvaluationOverallEvaluationId(
      Long overallEvaluationId);
}
