package org.richardstallman.dvback.domain.evaluation.domain.overall.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OverallEvaluationUserInfoListResponseDto(
    @NotNull List<OverallEvaluationUserInfoResponseDto> evaluationInfos) {}
