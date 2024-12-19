package org.richardstallman.dvback.client.firebase.service;

public interface FirebaseMessagingService {

  void sendNotification(Long userId, String title, String body);
}
