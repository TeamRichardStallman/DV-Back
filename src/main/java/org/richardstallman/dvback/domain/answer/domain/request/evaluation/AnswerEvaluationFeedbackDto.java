package org.richardstallman.dvback.domain.answer.domain.request.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AnswerEvaluationFeedbackDto(
    @NotNull @JsonProperty("strengths") String strengths,
    @NotNull @JsonProperty("improvement") String improvement,
    @NotNull @JsonProperty("suggestion") String suggestion) {}
