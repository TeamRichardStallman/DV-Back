package org.richardstallman.dvback.client.firebase.repository;

import org.richardstallman.dvback.client.firebase.domain.FcmTokenDomain;

public interface FcmTokenRepository {

  FcmTokenDomain save(FcmTokenDomain fcmTokenDomain);

  FcmTokenDomain findByUserId(Long userId);

  void deleteByUserId(Long userId);
}
