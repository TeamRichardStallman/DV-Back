package org.richardstallman.dvback.domain.answer.domain.request.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AnswerEvaluationCriteriaDto(
    @NotNull @JsonProperty("score") Integer score,
    @NotNull @JsonProperty("rationale") String rationale) {}
