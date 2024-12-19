package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record OverallEvaluationResultRequestDto(
    @JsonProperty("user_id") @NotNull Long userId,
    @JsonProperty("interview_id") @NotNull Long interviewId,
    @JsonProperty("overall_evaluation") OverallEvaluationResultDto overallEvaluation) {}
