package org.richardstallman.dvback.client.firebase.service;

public interface FcmTokenService {

  void createFcmToken(Long userId, String token);

  void deleteFcmToken(Long userId, String token);
}
