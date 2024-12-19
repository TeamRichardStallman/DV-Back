package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record OverallEvaluationResultDto(
    @JsonProperty("text_overall") @NotNull OverallEvaluationResultTextDto textOverall,
    @JsonProperty("voice_overall") OverallEvaluationResultVoiceDto voiceOverall) {}
