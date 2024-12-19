package org.richardstallman.dvback.domain.coupon.service;

import jakarta.validation.Valid;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponCreateRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.request.CouponUseRequestDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponInfoResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponListExpiredResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponListSimpleResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponListUsedResponseDto;
import org.richardstallman.dvback.domain.coupon.domain.response.CouponUseResponseDto;

public interface CouponService {

  CouponInfoResponseDto createCoupon(CouponCreateRequestDto couponCreateRequestDto);

  CouponUseResponseDto useCoupon(@Valid CouponUseRequestDto couponUseRequestDto, Long userId);

  CouponListSimpleResponseDto getSimpleCouponList(Long userId);

  CouponListUsedResponseDto getUsedCouponList(Long userId);

  CouponListExpiredResponseDto getExpiredCouponList(Long userId);
}
