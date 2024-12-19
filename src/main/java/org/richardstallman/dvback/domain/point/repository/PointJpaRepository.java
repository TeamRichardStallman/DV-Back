package org.richardstallman.dvback.domain.point.repository;

import org.richardstallman.dvback.domain.point.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {

  PointEntity findByUserId(Long userId);
}
