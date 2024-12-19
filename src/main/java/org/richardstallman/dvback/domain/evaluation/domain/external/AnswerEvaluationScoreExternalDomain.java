package org.richardstallman.dvback.domain.evaluation.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AnswerEvaluationScoreExternalDomain {

  @JsonProperty("appropriate_response")
  private AnswerEvaluationCriteriaExternalDomain appropriateResponse;

  @JsonProperty("logical_flow")
  private AnswerEvaluationCriteriaExternalDomain logicalFlow;

  @JsonProperty("key_terms")
  private AnswerEvaluationCriteriaExternalDomain keyTerms;

  @JsonProperty("consistency")
  private AnswerEvaluationCriteriaExternalDomain consistency;

  @JsonProperty("grammatical_errors")
  private AnswerEvaluationCriteriaExternalDomain grammaticalErrors;
}
