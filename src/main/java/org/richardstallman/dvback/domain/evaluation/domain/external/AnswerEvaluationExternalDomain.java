package org.richardstallman.dvback.domain.evaluation.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AnswerEvaluationExternalDomain {

  @NotNull @JsonProperty("question_id")
  long questionId;

  @NotNull @JsonProperty("scores")
  AnswerEvaluationScoreExternalDomain answerEvaluationScoreExternalDomain;

  @NotNull @JsonProperty("feedback")
  AnswerEvaluationFeedbackExternalDomain answerEvaluationFeedbackExternalDomain;
}
