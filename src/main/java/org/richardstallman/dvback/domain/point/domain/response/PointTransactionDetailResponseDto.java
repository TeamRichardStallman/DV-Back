package org.richardstallman.dvback.domain.point.domain.response;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.PointTransactionType;

public record PointTransactionDetailResponseDto(
    @NotNull long pointTransactionId,
    @NotNull int amount,
    @NotNull PointTransactionType pointTransactionType,
    @NotNull String description) {}
