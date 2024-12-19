package org.richardstallman.dvback.domain.coupon.repository;

import java.util.List;
import org.richardstallman.dvback.domain.coupon.domain.CouponDomain;

public interface CouponRepository {

  CouponDomain save(CouponDomain couponDomain);

  CouponDomain findById(Long couponId);

  List<CouponDomain> findSimpleListByUserId(Long userId);

  List<CouponDomain> findUsedListByUserId(Long userId);

  List<CouponDomain> findExpiredListByUserId(Long userId);
}
