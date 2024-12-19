package org.richardstallman.dvback.global.oauth;

import java.util.Map;

public class KakaoUserInfo {

  private final Map<String, Object> attributes;

  public KakaoUserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public String getId() {
    return String.valueOf(attributes.get("id"));
  }

  public String getEmail() {
    Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
    if (account == null || account.get("email") == null) {
      throw new IllegalStateException("이메일 정보가 없습니다.");
    }
    return (String) account.get("email");
  }
}
