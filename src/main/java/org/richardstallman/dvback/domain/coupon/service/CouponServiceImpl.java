package org.richardstallman.dvback.domain.coupon.service;

import static org.richardstallman.dvback.global.util.TimeUtil.generateExpirationDateTime;
import static org.richardstallman.dvback.global.util.TimeUtil.getCurrentDateTime;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponUseRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponDetailSimpleResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponDetailUsedResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponInfoResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponListExpiredResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponListSimpleResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponListUsedResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponUseResponseDto;
import org.richardstallman.dvback.domain.coupon.repository.CouponRepository;
import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;
import org.richardstallman.dvback.domain.ticket.service.TicketService;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

  private final CouponConverter couponConverter;
  private final CouponRepository couponRepository;
  private final UserService userService;
  private final UserConverter userConverter;
  private final TicketService ticketService;

  @Override
  public CouponInfoResponseDto createCoupon(CouponCreateRequestDto couponCreateRequestDto) {
    UserDomain userDomain =
        userConverter.fromResponseDtoToDomain(
            userService.getUserInfo(couponCreateRequestDto.userId()));
    LocalDateTime now = getCurrentDateTime();
    LocalDateTime expireAt = generateExpirationDateTime(now);
    CouponDomain couponDomain =
        couponConverter.fromCreateRequestDtoToDomain(
            couponCreateRequestDto, userDomain, now, expireAt);
    return couponConverter.fromDomainToInfoResponseDto(couponRepository.save(couponDomain));
  }

  @Override
  @Transactional
  public CouponUseResponseDto useCoupon(CouponUseRequestDto couponUseRequestDto, Long userId) {
    CouponDomain couponDomain = couponRepository.findById(couponUseRequestDto.couponId());

    validateCoupon(couponDomain);

    couponDomain = couponConverter.fromUnUsedToUsed(couponDomain, getCurrentDateTime());

    TicketTransactionRequestDto ticketTransactionRequestDto =
        new TicketTransactionRequestDto(
            couponDomain.getChargeAmount(),
            TicketTransactionType.CHARGE,
            TicketTransactionMethod.COUPON,
            couponDomain.getInterviewMode(),
            couponDomain.getInterviewAssetType(),
            "");

    CouponDomain usedCoupon = couponRepository.save(couponDomain);

    TicketTransactionResponseDto ticketTransactionResponseDto =
        ticketService.chargeTicket(ticketTransactionRequestDto, userId);

    return couponConverter.generateUseResponseDto(
        couponConverter.fromDomainToInfoResponseDto(usedCoupon), ticketTransactionResponseDto);
  }

  @Override
  public CouponListSimpleResponseDto getSimpleCouponList(Long userId) {
    validateCouponsForUser(userId);
    List<CouponDetailSimpleResponseDto> couponDetailSimpleResponseDtos =
        couponRepository.findSimpleListByUserId(userId).stream()
            .map(couponConverter::fromDomainToDetailSimpleResponseDto)
            .toList();
    return new CouponListSimpleResponseDto(couponDetailSimpleResponseDtos);
  }

  @Override
  public CouponListUsedResponseDto getUsedCouponList(Long userId) {
    validateCouponsForUser(userId);
    List<CouponDetailUsedResponseDto> couponDetailUsedResponseDtos =
        couponRepository.findUsedListByUserId(userId).stream()
            .map(couponConverter::fromDomainToDetailUsedResponseDto)
            .toList();
    return new CouponListUsedResponseDto(couponDetailUsedResponseDtos);
  }

  @Override
  public CouponListExpiredResponseDto getExpiredCouponList(Long userId) {
    validateCouponsForUser(userId);
    List<CouponDetailSimpleResponseDto> couponDetailSimpleResponseDtos =
        couponRepository.findExpiredListByUserId(userId).stream()
            .map(couponConverter::fromDomainToDetailSimpleResponseDto)
            .toList();
    return new CouponListExpiredResponseDto(couponDetailSimpleResponseDtos);
  }

  private void validateCoupon(CouponDomain couponDomain) {
    if (couponDomain.getExpireAt().isBefore(LocalDateTime.now())) {
      couponDomain = couponRepository.save(couponConverter.fromUnExpiredToExpired(couponDomain));
    }
    if (couponDomain.isUsed()) {
      throw new IllegalStateException("Coupon (" + couponDomain.getCouponId() + ") already used");
    }
    if (couponDomain.isExpired()) {
      throw new IllegalStateException(
          "Coupon ("
              + couponDomain.getCouponId()
              + ") expired at ("
              + couponDomain.getExpireAt()
              + ")");
    }
  }

  private void validateCouponsForUser(Long userId) {
    List<CouponDomain> coupons = couponRepository.findSimpleListByUserId(userId);
    for (CouponDomain couponDomain : coupons) {
      if (couponDomain.getExpireAt().isBefore(LocalDateTime.now())) {
        couponRepository.save(couponConverter.fromUnExpiredToExpired(couponDomain));
      }
    }
  }
}
