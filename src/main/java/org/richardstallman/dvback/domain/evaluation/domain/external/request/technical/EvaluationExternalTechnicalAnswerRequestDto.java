package org.richardstallman.dvback.domain.evaluation.domain.external.request.technical;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record EvaluationExternalTechnicalAnswerRequestDto(
    @JsonProperty("question_id") @NotNull Long questionId,
    @JsonProperty("answer") @NotNull EvaluationExternalTechnicalAnswerDto answer) {}
