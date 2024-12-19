package org.richardstallman.dvback.domain.interview.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record InterviewEvaluationListResponseDto(
    @NotNull List<InterviewEvaluationResponseDto> interviews) {}
