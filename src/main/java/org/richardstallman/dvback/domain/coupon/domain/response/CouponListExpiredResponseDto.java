package org.richardstallman.dvback.domain.coupon.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CouponListExpiredResponseDto(@NotNull List<CouponDetailSimpleResponseDto> coupons) {}
