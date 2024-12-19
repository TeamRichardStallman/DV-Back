package org.richardstallman.dvback.domain.evaluation.domain.answer;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;

@Builder
@Getter
public class AnswerEvaluationDomain {

  private final Long answerEvaluationId;
  private final QuestionDomain questionDomain;
  private final String answerFeedbackStrength;
  private final String answerFeedbackImprovement;
  private final String answerFeedbackSuggestion;
  private final OverallEvaluationDomain overallEvaluationDomain;
}
