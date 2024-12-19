package org.richardstallman.dvback.client.firebase.service;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.client.firebase.domain.FcmTokenDomain;
import org.richardstallman.dvback.client.firebase.repository.FcmTokenRepository;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {
  private final FcmTokenRepository fcmTokenRepository;
  private final UserRepository userRepository;

  @Override
  public void createFcmToken(Long userId, String token) {
    UserDomain user = userRepository.findById(userId).orElse(null);
    FcmTokenDomain fcmTokenDomain = getDomain(userId);
    if (fcmTokenDomain == null) {
      fcmTokenRepository.save(FcmTokenDomain.builder().userDomain(user).token(token).build());
      return;
    }
    fcmTokenRepository.save(
        FcmTokenDomain.builder()
            .fcmTokenId(fcmTokenDomain.getFcmTokenId())
            .userDomain(getUser(userId))
            .token(token)
            .build());
  }

  @Override
  public void deleteFcmToken(Long userId, String token) {
    fcmTokenRepository.deleteByUserId(userId);
  }

  private UserDomain getUser(Long userId) {
    return userRepository.findById(userId).orElse(null);
  }

  private FcmTokenDomain getDomain(Long userId) {
    return fcmTokenRepository.findByUserId(userId);
  }
}
