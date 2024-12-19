package org.richardstallman.dvback.domain.evaluation.repository.overall;

import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;

public interface OverallEvaluationRepository {

  OverallEvaluationDomain save(OverallEvaluationDomain overallEvaluationDomain);

  OverallEvaluationDomain findByInterviewId(Long interviewId);

  OverallEvaluationDomain findById(Long overallEvaluationId);
}
