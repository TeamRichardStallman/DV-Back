package org.richardstallman.dvback.domain.point.repository.transaction;

import java.util.List;
import org.richardstallman.dvback.domain.point.entity.PointTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointTransactionJpaRepository extends JpaRepository<PointTransactionEntity, Long> {

  List<PointTransactionEntity> findByUserId(Long userId);
}
