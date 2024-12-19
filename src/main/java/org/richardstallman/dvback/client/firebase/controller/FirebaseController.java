package org.richardstallman.dvback.client.firebase.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.firebase.domain.FcmSendDto;
import org.richardstallman.dvback.client.firebase.domain.request.FcmTokenCreateRequestDto;
import org.richardstallman.dvback.client.firebase.domain.response.FcmTokenCreateResponseDto;
import org.richardstallman.dvback.client.firebase.service.FcmTokenService;
import org.richardstallman.dvback.client.firebase.service.FirebaseMessagingService;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FirebaseController {

  private final FirebaseMessagingService firebaseMessagingService;
  private final FcmTokenService fcmTokenService;

  @PostMapping("/test")
  public String sendNotification(
      @AuthenticationPrincipal Long userId, @RequestBody FcmSendDto fcmSendDto) {
    firebaseMessagingService.sendNotification(userId, fcmSendDto.title(), fcmSendDto.body());
    return "Notification sent successfully!";
  }

  @PostMapping("/token")
  public ResponseEntity<DvApiResponse<FcmTokenCreateResponseDto>> saveFcmToken(
      @AuthenticationPrincipal Long userId, @RequestBody FcmTokenCreateRequestDto requestDto) {
    log.info("Saving FCM token for user: " + userId + " with token: " + requestDto.token());
    fcmTokenService.createFcmToken(userId, requestDto.token());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            DvApiResponse.of(
                ResponseCode.SUCCESS, new FcmTokenCreateResponseDto("Token saved successfully!")));
  }

  @DeleteMapping("/token")
  public String deleteFcmToken(
      @AuthenticationPrincipal Long userId, @RequestBody Map<String, String> token) {
    log.info("Deleting FCM token for user: " + userId + " with token: " + token.get("token"));
    fcmTokenService.deleteFcmToken(userId, token.get("token"));
    return "Token removed successfully!";
  }
}
