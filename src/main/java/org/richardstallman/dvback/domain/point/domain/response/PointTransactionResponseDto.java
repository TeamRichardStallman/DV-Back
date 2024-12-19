package org.richardstallman.dvback.domain.point.domain.response;

import jakarta.validation.constraints.NotNull;

public record PointTransactionResponseDto(
    @NotNull int currentBalance,
    @NotNull PointTransactionDetailResponseDto pointTransactionDetails) {}
