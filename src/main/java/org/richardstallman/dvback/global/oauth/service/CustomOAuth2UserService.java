package org.richardstallman.dvback.global.oauth.service;

import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.oauth.OAuth2CustomUser;
import org.richardstallman.dvback.global.oauth.OAuthAttributes;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;
  private final UserConverter userConverter;

  OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService =
      new DefaultOAuth2UserService();

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest)
      throws OAuth2AuthenticationException {
    log.info("OAuth2 인증 처리 시작 - 사용자 정보 로드 중 (CustomOAuth2UserService.loadUser())");

    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);
    Map<String, Object> attributes = oAuth2User.getAttributes();
    String userNameAttributeName =
        oAuth2UserRequest
            .getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
    OAuthAttributes extractAttributes =
        OAuthAttributes.of(userNameAttributeName, attributes, userConverter);

    UserEntity userEntity =
        userConverter.fromDomainToEntity(
            userRepository
                .findBySocialId(extractAttributes.getKakaoUserInfo().getId())
                .orElseGet(() -> saveUser(extractAttributes)));

    if (userEntity == null) {
      log.error("UserEntity가 null입니다. 사용자 정보가 제대로 로드되지 않았습니다.");
    } else {
      log.info("UserEntity가 성공적으로 로드되었습니다. userId: " + userEntity.getUserId());
    }

    return new OAuth2CustomUser(
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        attributes,
        userNameAttributeName,
        userEntity);
  }

  private UserDomain saveUser(OAuthAttributes attributes) {
    UserEntity createdUser = attributes.toEntity();
    return userRepository.save(userConverter.fromEntityToDomain(createdUser));
  }
}
