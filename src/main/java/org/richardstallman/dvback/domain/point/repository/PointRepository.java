package org.richardstallman.dvback.domain.point.repository;

import org.richardstallman.dvback.domain.point.domain.PointDomain;

public interface PointRepository {

  PointDomain save(PointDomain point);

  PointDomain findByUserId(Long userId);
}
