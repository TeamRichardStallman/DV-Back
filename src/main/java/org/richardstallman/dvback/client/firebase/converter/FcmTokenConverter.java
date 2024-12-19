package org.richardstallman.dvback.client.firebase.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.client.firebase.domain.FcmTokenDomain;
import org.richardstallman.dvback.client.firebase.entity.FcmTokenEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmTokenConverter {

  private final UserConverter userConverter;

  public FcmTokenDomain fromEntityToDomain(FcmTokenEntity fcmTokenEntity) {
    return FcmTokenDomain.builder()
        .fcmTokenId(fcmTokenEntity.getFcmTokenId())
        .userDomain(userConverter.fromEntityToDomain(fcmTokenEntity.getUser()))
        .token(fcmTokenEntity.getToken())
        .build();
  }

  public FcmTokenEntity fromDomainToEntity(FcmTokenDomain fcmTokenDomain) {
    return new FcmTokenEntity(
        fcmTokenDomain.getFcmTokenId(),
        userConverter.fromDomainToEntity(fcmTokenDomain.getUserDomain()),
        fcmTokenDomain.getToken());
  }
}
