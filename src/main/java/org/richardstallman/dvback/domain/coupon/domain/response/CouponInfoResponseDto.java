package org.richardstallman.dvback.domain.coupon.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;

public record CouponInfoResponseDto(
    @NotNull Long couponId,
    @NotNull Long userId,
    @NotNull int chargeAmount,
    @NotNull String couponName,
    @NotNull InterviewMode interviewMode,
    @NotNull String interviewModeKorean,
    @NotNull InterviewAssetType interviewAssetType,
    @NotNull String interviewAssetTypeKorean,
    @NotNull boolean isUsed,
    @NotNull boolean isExpired,
    @NotNull LocalDateTime generatedAt,
    LocalDateTime usedAt,
    LocalDateTime expireAt) {}
