package org.richardstallman.dvback.domain.evaluation.domain.response;

import jakarta.validation.constraints.NotNull;

public record EvaluationCriteriaResponseDto(
    @NotNull Long evaluationCriteriaId,
    @NotNull String evaluationCriteria,
    @NotNull String feedbackText,
    @NotNull int score) {}
