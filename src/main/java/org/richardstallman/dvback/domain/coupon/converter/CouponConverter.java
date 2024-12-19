package org.richardstallman.dvback.domain.coupon.converter;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponDetailSimpleResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponDetailUsedResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponInfoResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponUseResponseDto;
import org.richardstallman.dvback.domain.coupon.entity.CouponEntity;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.global.config.coupons.CouponsConfig.WelcomeCoupons;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponConverter {

  private final UserConverter userConverter;

  public CouponDomain fromEntityToDomain(CouponEntity couponEntity) {
    return CouponDomain.builder()
        .couponId(couponEntity.getCouponId())
        .chargeAmount(couponEntity.getChargeAmount())
        .userDomain(userConverter.fromEntityToDomain(couponEntity.getUser()))
        .couponName(couponEntity.getCouponName())
        .interviewMode(couponEntity.getInterviewMode())
        .interviewAssetType(couponEntity.getInterviewAssetType())
        .isUsed(couponEntity.isUsed())
        .isExpired(couponEntity.isExpired())
        .generatedAt(couponEntity.getGeneratedAt())
        .usedAt(couponEntity.isUsed() ? couponEntity.getUsedAt() : null)
        .expireAt(couponEntity.getExpireAt())
        .build();
  }

  public CouponEntity fromDomainToEntity(CouponDomain couponDomain) {
    return new CouponEntity(
        couponDomain.getCouponId(),
        userConverter.fromDomainToEntity(couponDomain.getUserDomain()),
        couponDomain.getChargeAmount(),
        couponDomain.getCouponName(),
        couponDomain.getInterviewMode(),
        couponDomain.getInterviewAssetType(),
        couponDomain.isUsed(),
        couponDomain.isExpired(),
        couponDomain.getGeneratedAt(),
        couponDomain.isUsed() ? couponDomain.getUsedAt() : null,
        couponDomain.getExpireAt());
  }

  public CouponDomain fromCreateRequestDtoToDomain(
      CouponCreateRequestDto couponCreateRequestDto,
      UserDomain userDomain,
      LocalDateTime generatedAt,
      LocalDateTime expireAt) {
    return CouponDomain.builder()
        .userDomain(userDomain)
        .chargeAmount(couponCreateRequestDto.chargeAmount())
        .couponName(couponCreateRequestDto.couponName())
        .interviewMode(couponCreateRequestDto.interviewMode())
        .interviewAssetType(couponCreateRequestDto.interviewAssetType())
        .isUsed(false)
        .isExpired(false)
        .generatedAt(generatedAt)
        .expireAt(expireAt)
        .build();
  }

  public CouponInfoResponseDto fromDomainToInfoResponseDto(CouponDomain couponDomain) {
    return new CouponInfoResponseDto(
        couponDomain.getCouponId(),
        couponDomain.getUserDomain().getUserId(),
        couponDomain.getChargeAmount(),
        couponDomain.getCouponName(),
        couponDomain.getInterviewMode(),
        couponDomain.getInterviewMode().getKoreanName(),
        couponDomain.getInterviewAssetType(),
        couponDomain.getInterviewAssetType().getKoreanName(),
        couponDomain.isUsed(),
        couponDomain.isExpired(),
        couponDomain.getGeneratedAt(),
        couponDomain.isUsed() ? couponDomain.getUsedAt() : null,
        couponDomain.getExpireAt());
  }

  public CouponUseResponseDto generateUseResponseDto(
      CouponInfoResponseDto couponInfoResponseDto,
      TicketTransactionResponseDto ticketTransactionResponseDto) {
    return new CouponUseResponseDto(couponInfoResponseDto, ticketTransactionResponseDto);
  }

  public CouponDomain fromUnExpiredToExpired(CouponDomain couponDomain) {
    return CouponDomain.builder()
        .couponId(couponDomain.getCouponId())
        .userDomain(couponDomain.getUserDomain())
        .chargeAmount(couponDomain.getChargeAmount())
        .couponName(couponDomain.getCouponName())
        .interviewMode(couponDomain.getInterviewMode())
        .interviewAssetType(couponDomain.getInterviewAssetType())
        .isUsed(couponDomain.isUsed())
        .isExpired(true)
        .generatedAt(couponDomain.getGeneratedAt())
        .usedAt(couponDomain.getUsedAt())
        .expireAt(couponDomain.getExpireAt())
        .build();
  }

  public CouponDomain fromUnUsedToUsed(CouponDomain couponDomain, LocalDateTime usedAt) {
    return CouponDomain.builder()
        .couponId(couponDomain.getCouponId())
        .userDomain(couponDomain.getUserDomain())
        .chargeAmount(couponDomain.getChargeAmount())
        .couponName(couponDomain.getCouponName())
        .interviewMode(couponDomain.getInterviewMode())
        .interviewAssetType(couponDomain.getInterviewAssetType())
        .isUsed(true)
        .isExpired(couponDomain.isExpired())
        .generatedAt(couponDomain.getGeneratedAt())
        .usedAt(usedAt)
        .expireAt(couponDomain.getExpireAt())
        .build();
  }

  public CouponDetailSimpleResponseDto fromDomainToDetailSimpleResponseDto(
      CouponDomain couponDomain) {
    return new CouponDetailSimpleResponseDto(
        couponDomain.getCouponId(),
        couponDomain.getChargeAmount(),
        couponDomain.getCouponName(),
        couponDomain.getInterviewMode().getKoreanName(),
        couponDomain.getInterviewAssetType().getKoreanName(),
        couponDomain.getExpireAt());
  }

  public CouponDetailUsedResponseDto fromDomainToDetailUsedResponseDto(CouponDomain couponDomain) {
    return new CouponDetailUsedResponseDto(
        couponDomain.getCouponId(),
        couponDomain.getChargeAmount(),
        couponDomain.getCouponName(),
        couponDomain.getInterviewMode().getKoreanName(),
        couponDomain.getInterviewAssetType().getKoreanName(),
        couponDomain.getUsedAt());
  }

  public CouponDomain fromWelcomeCouponToDomain(
      WelcomeCoupons welcomeCoupons,
      UserDomain userDomain,
      LocalDateTime generatedAt,
      LocalDateTime expireAt) {
    return CouponDomain.builder()
        .userDomain(userDomain)
        .chargeAmount(welcomeCoupons.getChargeAmount())
        .couponName(welcomeCoupons.getCouponName())
        .interviewMode(InterviewMode.valueOf(welcomeCoupons.getInterviewMode()))
        .interviewAssetType(InterviewAssetType.valueOf(welcomeCoupons.getInterviewAssetType()))
        .isUsed(false)
        .isExpired(false)
        .generatedAt(generatedAt)
        .expireAt(expireAt)
        .build();
  }
}
