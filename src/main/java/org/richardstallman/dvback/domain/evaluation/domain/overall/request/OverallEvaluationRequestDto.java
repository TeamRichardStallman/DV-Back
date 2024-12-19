package org.richardstallman.dvback.domain.evaluation.domain.overall.request;

import jakarta.validation.constraints.NotNull;

public record OverallEvaluationRequestDto(@NotNull Long interviewId) {}
