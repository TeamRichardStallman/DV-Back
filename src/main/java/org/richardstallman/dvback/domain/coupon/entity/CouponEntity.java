package org.richardstallman.dvback.domain.coupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupons")
public class CouponEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_seq")
  @SequenceGenerator(name = "coupon_seq", sequenceName = "coupon_id_seq", allocationSize = 1)
  private Long couponId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private int chargeAmount;
  private String couponName;

  @Enumerated(EnumType.STRING)
  private InterviewMode interviewMode;

  @Enumerated(EnumType.STRING)
  private InterviewAssetType interviewAssetType;

  private boolean isUsed;
  private boolean isExpired;
  private LocalDateTime generatedAt;
  private LocalDateTime usedAt;
  private LocalDateTime expireAt;
}
