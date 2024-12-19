package org.richardstallman.dvback.domain.evaluation.domain.response;

import jakarta.validation.constraints.NotNull;

public record AnswerEvaluationScoreResponseDto(
    @NotNull Long answerEvaluationScoreId,
    @NotNull String answerEvaluationScoreName,
    @NotNull int score,
    @NotNull String rationale,
    @NotNull String answerEvaluationType) {}
