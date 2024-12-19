package org.richardstallman.dvback.domain.evaluation.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AnswerEvaluationCriteriaExternalDomain {

  @JsonProperty("score")
  private int score;

  @JsonProperty("rationale")
  private String rationale;
}
