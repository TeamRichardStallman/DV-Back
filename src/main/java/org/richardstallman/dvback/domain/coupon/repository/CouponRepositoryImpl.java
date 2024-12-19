package org.richardstallman.dvback.domain.coupon.repository;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;
import org.richardstallman.dvback.domain.coupon.entity.CouponEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

  private final CouponJpaRepository couponJpaRepository;
  private final CouponConverter couponConverter;

  @Override
  public CouponDomain save(CouponDomain couponDomain) {
    CouponEntity couponEntity =
        couponJpaRepository.save(couponConverter.fromDomainToEntity(couponDomain));

    return couponConverter.fromEntityToDomain(couponEntity);
  }

  @Override
  public CouponDomain findById(Long couponId) {
    return couponConverter.fromEntityToDomain(
        Objects.requireNonNull(couponJpaRepository.findById(couponId).orElse(null)));
  }

  @Override
  public List<CouponDomain> findSimpleListByUserId(Long userId) {
    return couponJpaRepository
        .findByUserUserIdAndIsUsedFalseAndIsExpiredFalseOrderByCouponIdDesc(userId)
        .stream()
        .map(couponConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<CouponDomain> findUsedListByUserId(Long userId) {
    return couponJpaRepository.findByUserUserIdAndIsUsedTrueOrderByCouponIdDesc(userId).stream()
        .map(couponConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<CouponDomain> findExpiredListByUserId(Long userId) {
    return couponJpaRepository.findByUserUserIdAndIsExpiredTrueOrderByCouponIdDesc(userId).stream()
        .map(couponConverter::fromEntityToDomain)
        .toList();
  }
}
