package org.richardstallman.dvback.domain.coupon.repository;

import java.util.List;
import org.richardstallman.dvback.domain.coupon.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponJpaRepository extends JpaRepository<CouponEntity, Long> {

  List<CouponEntity> findByUserUserIdAndIsUsedFalseAndIsExpiredFalseOrderByCouponIdDesc(
      Long userId);

  List<CouponEntity> findByUserUserIdAndIsUsedTrueOrderByCouponIdDesc(Long userId);

  List<CouponEntity> findByUserUserIdAndIsExpiredTrueOrderByCouponIdDesc(Long userId);
}
