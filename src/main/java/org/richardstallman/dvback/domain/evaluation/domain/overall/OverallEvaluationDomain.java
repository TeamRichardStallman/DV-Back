package org.richardstallman.dvback.domain.evaluation.domain.overall;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;

@Builder
@Getter
public class OverallEvaluationDomain {

  private final Long overallEvaluationId;
  private InterviewDomain interviewDomain;
}
