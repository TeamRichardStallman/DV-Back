package org.richardstallman.dvback.domain.answer.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationDto;

public record AnswerEvaluationRequestDto(
    @NotNull @JsonProperty("user_id") Long userId,
    @NotNull @JsonProperty("interview_id") Long interviewId,
    @NotNull @JsonProperty("question_id") Long questionId,
    @NotNull @JsonProperty("interview_method") String interviewMethod,
    @NotNull @JsonProperty("answer") AnswerEvaluationDto answer) {}
