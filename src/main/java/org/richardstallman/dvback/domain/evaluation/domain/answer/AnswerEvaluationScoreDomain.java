package org.richardstallman.dvback.domain.evaluation.domain.answer;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationType;

@Builder
@Getter
public class AnswerEvaluationScoreDomain {

  private Long answerEvaluationScoreId;
  private AnswerEvaluationScore answerEvaluationScoreName;
  private int score;
  private String rationale;
  private AnswerEvaluationDomain answerEvaluationDomain;
  private AnswerEvaluationType answerEvaluationType;
}
