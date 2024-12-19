package org.richardstallman.dvback.domain.coupon.domain.response;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;

public record CouponUseResponseDto(
    @NotNull CouponInfoResponseDto usedCouponInfo,
    @NotNull TicketTransactionResponseDto chargedTicketTransactionInfo) {}
