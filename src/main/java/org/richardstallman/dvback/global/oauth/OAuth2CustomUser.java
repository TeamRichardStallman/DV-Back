package org.richardstallman.dvback.global.oauth;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class OAuth2CustomUser extends DefaultOAuth2User {

  private final UserEntity userEntity;

  public OAuth2CustomUser(
      Collection<? extends GrantedAuthority> authorities,
      Map<String, Object> attributes,
      String nameAttributeKey,
      UserEntity userEntity) {
    super(authorities, attributes, nameAttributeKey);
    this.userEntity = userEntity;
  }

  public Long getUserId() {
    return userEntity.getUserId();
  }
}
