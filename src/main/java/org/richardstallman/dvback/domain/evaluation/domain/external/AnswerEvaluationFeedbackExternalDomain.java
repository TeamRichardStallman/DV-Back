package org.richardstallman.dvback.domain.evaluation.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AnswerEvaluationFeedbackExternalDomain {

  @JsonProperty("strengths")
  private String strength;

  @JsonProperty("improvement")
  private String improvement;

  @JsonProperty("suggestion")
  private String suggestion;
}
