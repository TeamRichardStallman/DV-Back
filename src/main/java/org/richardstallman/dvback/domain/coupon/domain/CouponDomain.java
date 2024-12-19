package org.richardstallman.dvback.domain.coupon.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Builder
@Getter
public class CouponDomain {

  private final Long couponId;
  private final UserDomain userDomain;
  private final int chargeAmount;
  private final String couponName;
  private InterviewMode interviewMode;
  private InterviewAssetType interviewAssetType;
  private boolean isUsed;
  private boolean isExpired;
  private LocalDateTime generatedAt;
  private LocalDateTime usedAt;
  private LocalDateTime expireAt;
}
