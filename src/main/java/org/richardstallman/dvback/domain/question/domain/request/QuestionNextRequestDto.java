package org.richardstallman.dvback.domain.question.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerPreviousRequestDto;

public record QuestionNextRequestDto(
    @NotNull Long interviewId,
    @NotNull Long answerQuestionId,
    Long nextQuestionId,
    @NotNull AnswerPreviousRequestDto answer) {}
