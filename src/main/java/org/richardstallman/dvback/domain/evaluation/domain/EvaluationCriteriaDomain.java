package org.richardstallman.dvback.domain.evaluation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.EvaluationCriteria;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationCriteriaDomain {

  private Long evaluationCriteriaId;
  private EvaluationCriteria evaluationCriteria;
  private String feedbackText;
  private int score;
  private OverallEvaluationDomain overallEvaluationDomain;
}
