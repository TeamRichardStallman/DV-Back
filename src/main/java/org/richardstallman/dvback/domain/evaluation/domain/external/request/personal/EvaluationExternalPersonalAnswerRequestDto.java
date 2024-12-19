package org.richardstallman.dvback.domain.evaluation.domain.external.request.personal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record EvaluationExternalPersonalAnswerRequestDto(
    @JsonProperty("question_id") @NotNull Long questionId,
    @JsonProperty("answer") @NotNull EvaluationExternalPersonalAnswerDto answer) {}
