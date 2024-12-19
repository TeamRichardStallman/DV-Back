package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record OverallEvaluationResultCriteriaDto(
    @NotNull @JsonProperty("score") Integer score,
    @NotNull @JsonProperty("rationale") String rationale) {}
