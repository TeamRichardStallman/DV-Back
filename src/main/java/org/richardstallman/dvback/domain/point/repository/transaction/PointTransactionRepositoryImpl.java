package org.richardstallman.dvback.domain.point.repository.transaction;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.point.converter.PointTransactionConverter;
import org.richardstallman.dvback.domain.point.domain.PointTransactionDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointTransactionRepositoryImpl implements PointTransactionRepository {

  private final PointTransactionConverter pointTransactionConverter;
  private final PointTransactionJpaRepository pointTransactionJpaRepository;

  @Override
  public PointTransactionDomain save(PointTransactionDomain pointTransactionDomain) {
    return pointTransactionConverter.fromEntityToDomain(
        pointTransactionJpaRepository.save(
            pointTransactionConverter.fromDomainToEntity(pointTransactionDomain)));
  }

  @Override
  public List<PointTransactionDomain> findByUserId(Long userId) {
    return pointTransactionJpaRepository.findByUserId(userId).stream()
        .map(pointTransactionConverter::fromEntityToDomain)
        .toList();
  }
}
