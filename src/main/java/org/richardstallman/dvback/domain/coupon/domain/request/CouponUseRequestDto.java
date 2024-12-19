package org.richardstallman.dvback.domain.coupon.domain.request;

import jakarta.validation.constraints.NotNull;

public record CouponUseRequestDto(@NotNull Long couponId) {}
