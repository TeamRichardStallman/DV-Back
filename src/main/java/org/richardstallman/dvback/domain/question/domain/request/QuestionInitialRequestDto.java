package org.richardstallman.dvback.domain.question.domain.request;

import jakarta.validation.constraints.NotNull;

public record QuestionInitialRequestDto(@NotNull Long interviewId) {}
