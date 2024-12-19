package org.richardstallman.dvback.client.firebase.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.ByteArrayInputStream;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

  @Value("${FIREBASE_ADMIN_SDK_PROJECT_ID}")
  private String projectId;

  @Value("${FIREBASE_ADMIN_SDK_PRIVATE_KEY_ID}")
  private String privateKeyId;

  @Value("${FIREBASE_ADMIN_SDK_PRIVATE_KEY}")
  private String privateKey;

  @Value("${FIREBASE_ADMIN_SDK_CLIENT_EMAIL}")
  private String clientEmail;

  @Value("${FIREBASE_ADMIN_SDK_CLIENT_ID}")
  private String clientId;

  @Value("${FIREBASE_ADMIN_AUTH_URI}")
  private String authUri;

  @Value("${FIREBASE_ADMIN_TOKEN_URI}")
  private String tokenUri;

  @Value("${FIREBASE_ADMIN_AUTH_PROVIDER_X509_CERT_URL}")
  private String authProviderX509CertUrl;

  @Value("${FIREBASE_ADMIN_CLIENT_X509_CERT_URL}")
  private String clientX509CertUrl;

  @PostConstruct
  public void initializeFirebase() {
    try {
      if (FirebaseApp.getApps().isEmpty()) {
        Map<String, Object> firebaseConfig =
            Map.of(
                "type", "service_account",
                "project_id", projectId,
                "private_key_id", privateKeyId,
                "private_key", privateKey.replace("\\n", "\n"),
                "client_email", clientEmail,
                "client_id", clientId,
                "auth_uri", authUri,
                "token_uri", tokenUri,
                "auth_provider_x509_cert_url", authProviderX509CertUrl,
                "client_x509_cert_url", clientX509CertUrl);

        ByteArrayInputStream serviceAccount =
            new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(firebaseConfig));

        FirebaseOptions options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Failed to initialize Firebase Admin SDK", e);
    }
  }
}
