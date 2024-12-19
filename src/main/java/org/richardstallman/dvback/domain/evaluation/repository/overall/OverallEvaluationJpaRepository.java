package org.richardstallman.dvback.domain.evaluation.repository.overall;

import org.richardstallman.dvback.domain.evaluation.entity.overall.OverallEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverallEvaluationJpaRepository
    extends JpaRepository<OverallEvaluationEntity, Long> {

  OverallEvaluationEntity findByInterviewInterviewId(Long interviewInterviewId);
}
