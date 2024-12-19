package org.richardstallman.dvback.client.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.firebase.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

  private final FcmTokenRepository fcmTokenRepository;

  @Override
  @Transactional
  public void sendNotification(Long userId, String title, String body) {
    try {
      String userToken = fcmTokenRepository.findByUserId(userId).getToken();
      Message message =
          Message.builder()
              .setToken(userToken)
              .setNotification(Notification.builder().setTitle(title).setBody(body).build())
              .build();

      String response = FirebaseMessaging.getInstance().send(message);
      log.info("Successfully sent message: {}", response);
    } catch (FirebaseMessagingException e) {
      if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
        log.info("Token is no longer valid");
        fcmTokenRepository.deleteByUserId(userId);
      } else {
        log.error("Failed to send message: {}", e.getMessage());
      }
    } catch (Exception e) {
      log.error(
          "Unexpected error occurred while sending FCM message for userId: {}. Stacktrace: ",
          userId,
          e);
    }
  }
}
