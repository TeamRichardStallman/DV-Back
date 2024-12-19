package org.richardstallman.dvback.domain.question.domain.response;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewQuestionResponseDto;

public record QuestionResponseDto(
    @NotNull InterviewQuestionResponseDto interview,
    @NotNull Long currentQuestionId,
    @NotNull String currentQuestionText,
    String currentQuestionS3AudioUrl,
    Long nextQuestionId,
    String nextQuestionText,
    String nextQuestionS3AudioUrl,
    @NotNull Boolean hasNext) {}
