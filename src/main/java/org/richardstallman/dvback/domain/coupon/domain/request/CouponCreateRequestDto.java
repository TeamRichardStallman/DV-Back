package org.richardstallman.dvback.domain.coupon.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.CouponType;

public record CouponCreateRequestDto(
    @NotNull Long userId,
    @NotNull int chargeAmount,
    @NotNull String couponName,
    @NotNull CouponType couponType) {}