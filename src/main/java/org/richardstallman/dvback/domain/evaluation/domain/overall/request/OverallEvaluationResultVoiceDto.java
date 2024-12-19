package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record OverallEvaluationResultVoiceDto(
    @NotNull @JsonProperty("fluency") OverallEvaluationResultCriteriaDto fluency,
    @NotNull @JsonProperty("clarity") OverallEvaluationResultCriteriaDto clarity,
    @NotNull @JsonProperty("word_repetition") OverallEvaluationResultCriteriaDto wordRepetition) {}
