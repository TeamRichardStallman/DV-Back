package org.richardstallman.dvback.domain.point.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.PointTransactionType;

public record PointTransactionRequestDto(
    @NotNull Long userId,
    @NotNull int amount,
    @NotNull PointTransactionType pointTransactionType,
    @NotNull String description) {}
