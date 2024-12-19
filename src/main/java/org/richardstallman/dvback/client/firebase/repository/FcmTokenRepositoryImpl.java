package org.richardstallman.dvback.client.firebase.repository;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.client.firebase.converter.FcmTokenConverter;
import org.richardstallman.dvback.client.firebase.domain.FcmTokenDomain;
import org.richardstallman.dvback.client.firebase.entity.FcmTokenEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryImpl implements FcmTokenRepository {

  private final FcmTokenConverter fcmTokenConverter;
  private final FcmTokenJpaRepository fcmTokenJpaRepository;

  @Override
  public FcmTokenDomain save(FcmTokenDomain fcmTokenDomain) {
    return fcmTokenConverter.fromEntityToDomain(
        fcmTokenJpaRepository.save(fcmTokenConverter.fromDomainToEntity(fcmTokenDomain)));
  }

  @Override
  public FcmTokenDomain findByUserId(Long userId) {
    FcmTokenEntity fcmTokenEntity = fcmTokenJpaRepository.findByUserUserId(userId);
    if (fcmTokenEntity == null) {
      return null;
    }
    return fcmTokenConverter.fromEntityToDomain(fcmTokenEntity);
  }

  @Override
  public void deleteByUserId(Long userId) {
    fcmTokenJpaRepository.deleteByUserUserId(userId);
  }
}
