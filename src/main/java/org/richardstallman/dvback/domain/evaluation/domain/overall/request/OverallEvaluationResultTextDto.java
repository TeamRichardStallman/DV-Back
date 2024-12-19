package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OverallEvaluationResultTextDto(
    @JsonProperty("job_fit") OverallEvaluationResultCriteriaDto jobFit,
    @JsonProperty("growth_potential") OverallEvaluationResultCriteriaDto growthPotential,
    @JsonProperty("work_attitude") OverallEvaluationResultCriteriaDto workAttitude,
    @JsonProperty("technical_depth") OverallEvaluationResultCriteriaDto technicalDepth,
    @JsonProperty("company_fit") OverallEvaluationResultCriteriaDto companyFit,
    @JsonProperty("adaptability") OverallEvaluationResultCriteriaDto adaptability,
    @JsonProperty("interpersonal_skills") OverallEvaluationResultCriteriaDto interpersonalSkills,
    @JsonProperty("growth_attitude") OverallEvaluationResultCriteriaDto growthAttitude) {}
