package org.richardstallman.dvback.domain.interview.domain.response;

import jakarta.validation.constraints.NotNull;

public record InterviewEvaluationResponseDto(
    @NotNull Long interviewId, @NotNull String interviewTitle) {}
