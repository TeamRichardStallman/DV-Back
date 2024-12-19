package org.richardstallman.dvback.domain.point.repository;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.point.converter.PointConverter;
import org.richardstallman.dvback.domain.point.domain.PointDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

  private final PointConverter pointConverter;
  private final PointJpaRepository pointJpaRepository;

  @Override
  public PointDomain save(PointDomain point) {
    return pointConverter.fromEntityToDomain(
        pointJpaRepository.save(pointConverter.fromDomainToEntity(point)));
  }

  @Override
  public PointDomain findByUserId(Long userId) {
    return pointConverter.fromEntityToDomain(pointJpaRepository.findByUserId(userId));
  }
}
