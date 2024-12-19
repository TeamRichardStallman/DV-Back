package org.richardstallman.dvback.global.oauth;

import java.util.Map;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.entity.UserEntity;

@Getter
public class OAuthAttributes {

  private final String nameAttributeKey;
  private final KakaoUserInfo kakaoUserInfo;
  private final UserConverter userConverter;

  private OAuthAttributes(
      String nameAttributeKey, KakaoUserInfo kakaoUserInfo, UserConverter userConverter) {
    this.nameAttributeKey = nameAttributeKey;
    this.kakaoUserInfo = kakaoUserInfo;
    this.userConverter = userConverter;
  }

  public static OAuthAttributes of(
      String userNameAttributeName, Map<String, Object> attributes, UserConverter userConverter) {
    return ofKakao(userNameAttributeName, attributes, userConverter);
  }

  private static OAuthAttributes ofKakao(
      String userNameAttributeName, Map<String, Object> attributes, UserConverter userConverter) {
    return new OAuthAttributes(userNameAttributeName, new KakaoUserInfo(attributes), userConverter);
  }

  public UserEntity toEntity() {
    return userConverter.kakaoInfoToUserEntity(kakaoUserInfo);
  }
}
