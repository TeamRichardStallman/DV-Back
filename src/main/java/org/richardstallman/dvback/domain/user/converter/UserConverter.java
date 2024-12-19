package org.richardstallman.dvback.domain.user.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.request.UserUpdateRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserLoginResponseDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.global.oauth.KakaoUserInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

  public UserEntity fromDomainToEntity(UserDomain userDomain) {
    return new UserEntity(
        userDomain.getUserId(),
        userDomain.getSocialId(),
        userDomain.getEmail(),
        userDomain.getUsername(),
        userDomain.getName(),
        userDomain.getNickname(),
        userDomain.getS3ProfileImageObjectKey(),
        userDomain.getLeave(),
        userDomain.getGender(),
        userDomain.getBirthdate());
  }

  public UserDomain fromEntityToDomain(UserEntity userEntity) {
    return UserDomain.builder()
        .userId(userEntity.getUserId())
        .socialId(userEntity.getSocialId())
        .email(userEntity.getEmail())
        .username(userEntity.getUsername())
        .name(userEntity.getName())
        .nickname(userEntity.getNickname())
        .s3ProfileImageObjectKey(userEntity.getS3ProfileImageObjectKey())
        .leave(userEntity.getLeave())
        .gender(userEntity.getGender())
        .birthdate(userEntity.getBirthdate())
        .build();
  }

  public UserEntity kakaoInfoToUserEntity(KakaoUserInfo kakaoUserInfo) {
    return new UserEntity(kakaoUserInfo.getId(), kakaoUserInfo.getEmail());
  }

  public UserResponseDto fromDomainWithPreSignedUrlToDto(
      UserDomain userDomain, String preSignedUrl) {
    return new UserResponseDto(
        userDomain.getUserId(),
        userDomain.getSocialId(),
        userDomain.getEmail(),
        userDomain.getUsername(),
        userDomain.getName(),
        userDomain.getNickname(),
        preSignedUrl,
        userDomain.getLeave(),
        userDomain.getGender(),
        userDomain.getBirthdate());
  }

  public UserDomain fromResponseDtoToDomain(UserResponseDto userResponseDto) {
    return UserDomain.builder()
        .userId(userResponseDto.userId())
        .socialId(userResponseDto.socialId())
        .email(userResponseDto.email())
        .username(userResponseDto.username())
        .name(userResponseDto.name())
        .nickname(userResponseDto.nickname())
        .s3ProfileImageObjectKey(extractObjectKeyFromUrl(userResponseDto.s3ProfileImageUrl()))
        .leave(userResponseDto.leave())
        .gender(userResponseDto.gender())
        .birthdate(userResponseDto.birthdate())
        .build();
  }

  public UserLoginResponseDto fromResponseDtoToLoginResponseDto(
      UserResponseDto userResponseDto, String type) {
    return new UserLoginResponseDto(
        type,
        userResponseDto.userId(),
        userResponseDto.socialId(),
        userResponseDto.email(),
        userResponseDto.username(),
        userResponseDto.name(),
        userResponseDto.nickname(),
        userResponseDto.s3ProfileImageUrl(),
        userResponseDto.leave(),
        userResponseDto.gender(),
        userResponseDto.birthdate());
  }

  public UserDomain updateUser(UserDomain userDomain, UserUpdateRequestDto userUpdateRequestDto) {
    return UserDomain.builder()
        .userId(userDomain.getUserId())
        .socialId(userDomain.getSocialId())
        .email(userDomain.getEmail())
        .username(userDomain.getUsername())
        .name(userUpdateRequestDto.name())
        .nickname(userUpdateRequestDto.nickname())
        .s3ProfileImageObjectKey(
            userUpdateRequestDto.s3ProfileImageObjectKey() == null
                ? userDomain.getS3ProfileImageObjectKey()
                : extractObjectKeyFromUrl(userUpdateRequestDto.s3ProfileImageObjectKey()))
        .leave(userDomain.getLeave())
        .gender(userUpdateRequestDto.gender())
        .birthdate(userUpdateRequestDto.birthdate())
        .build();
  }

  public UserLoginResponseDto forSignUp(UserResponseDto userResponseDto, String type) {
    return new UserLoginResponseDto(
        type, userResponseDto.userId(), null, null, null, null, null, null, null, null, null);
  }

  private String extractObjectKeyFromUrl(String s3ProfileImageUrl) {
    if (s3ProfileImageUrl == null) {
      return null;
    }
    int queryIndex = s3ProfileImageUrl.indexOf('?');
    String urlWithoutQuery =
        queryIndex != -1 ? s3ProfileImageUrl.substring(0, queryIndex) : s3ProfileImageUrl;

    int lastSlashIndex = urlWithoutQuery.lastIndexOf('/');
    return lastSlashIndex != -1 ? urlWithoutQuery.substring(lastSlashIndex + 1) : urlWithoutQuery;
  }
}
