package org.richardstallman.dvback.domain.evaluation.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class OverallEvaluationExternalDomain {

  @JsonProperty("job_fit")
  private EvaluationCriteriaExternalDomain jobFit;

  @JsonProperty("growth_potential")
  private EvaluationCriteriaExternalDomain growthPotential;

  @JsonProperty("work_attitude")
  private EvaluationCriteriaExternalDomain workAttitude;

  @JsonProperty("technical_depth")
  private EvaluationCriteriaExternalDomain technicalDepth;
}
