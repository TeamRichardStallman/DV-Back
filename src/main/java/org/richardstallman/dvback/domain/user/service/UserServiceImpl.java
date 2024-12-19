package org.richardstallman.dvback.domain.user.service;

import static org.richardstallman.dvback.global.util.TimeUtil.generateExpirationDateTime;
import static org.richardstallman.dvback.global.util.TimeUtil.getCurrentDateTime;

import com.vane.badwordfiltering.BadWordFiltering;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.domain.coupon.converter.CouponConverter;
import org.richardstallman.dvback.domain.coupon.repository.CouponRepository;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.request.UserUpdateRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.config.coupons.CouponsConfig;
import org.richardstallman.dvback.global.oauth.service.CookieService;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final CookieService cookieService;
  private final TokenService tokenService;
  private final CouponRepository couponRepository;
  private final CouponsConfig couponsConfig;
  private final CouponConverter couponConverter;
  private final S3Service s3Service;

  @Override
  public UserResponseDto getUserInfo(Long userId) {
    log.info("getUserInfo");
    UserDomain userDomain = findUserById(userId);
    PreSignedUrlResponseDto preSignedUrl = null;
    if (userDomain != null && userDomain.getS3ProfileImageObjectKey() != null) {
      preSignedUrl =
          s3Service.getPreSignedUrlForImage(userDomain.getS3ProfileImageObjectKey(), userId);
    }
    return userConverter.fromDomainWithPreSignedUrlToDto(
        userDomain, preSignedUrl == null ? null : preSignedUrl.preSignedUrl());
  }

  @Override
  public void logout(HttpServletResponse response, String refreshToken) {
    cookieService.deleteCookie(response, "access_token");
    cookieService.deleteCookie(response, "refresh_token");
    tokenService.deleteRefreshTokenByRefreshToken(refreshToken);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public UserResponseDto createUserInfo(Long userId, UserRequestDto userRequestDto) {
    UserDomain user = findUserById(userId);

    validateName(userRequestDto.name());
    validateUsername(userRequestDto.username());
    validateNickname(userRequestDto.nickname());

    UserEntity userEntity = userConverter.fromDomainToEntity(user);

    UserEntity createdUser =
        userEntity.updatedUserEntity(
            userRequestDto.username(),
            userRequestDto.name(),
            userRequestDto.nickname(),
            userRequestDto.s3ProfileImageObjectKey(),
            userRequestDto.birthdate(),
            userRequestDto.gender());

    user = userRepository.save(userConverter.fromEntityToDomain(createdUser));

    LocalDateTime now = getCurrentDateTime();
    LocalDateTime expireAt = generateExpirationDateTime(now);
    for (CouponsConfig.WelcomeCoupons welcomeCoupons : couponsConfig.getWelcomeCoupons()) {
      couponRepository.save(
          couponConverter.fromWelcomeCouponToDomain(welcomeCoupons, user, now, expireAt));
    }

    PreSignedUrlResponseDto preSignedUrl = null;
    if (createdUser.getS3ProfileImageObjectKey() != null) {
      preSignedUrl =
          s3Service.getPreSignedUrlForImage(createdUser.getS3ProfileImageObjectKey(), userId);
    }

    return new UserResponseDto(
        createdUser.getUserId(),
        createdUser.getSocialId(),
        createdUser.getEmail(),
        createdUser.getUsername(),
        createdUser.getName(),
        createdUser.getNickname(),
        preSignedUrl == null ? null : preSignedUrl.preSignedUrl(),
        createdUser.getLeave(),
        createdUser.getGender(),
        createdUser.getBirthdate());
  }

  @Override
  public UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
    log.info("updateUserInfo: {}", userUpdateRequestDto);
    UserDomain user = findUserById(userId);
    validateName(userUpdateRequestDto.name());
    validateNickname(userUpdateRequestDto.nickname());

    UserDomain userDomain =
        userRepository.save(userConverter.updateUser(user, userUpdateRequestDto));

    PreSignedUrlResponseDto preSignedUrl =
        s3Service.getPreSignedUrlForImage(user.getS3ProfileImageObjectKey(), userId);

    UserResponseDto userResponseDto =
        new UserResponseDto(
            userDomain.getUserId(),
            userDomain.getSocialId(),
            userDomain.getEmail(),
            userDomain.getUsername(),
            userDomain.getName(),
            userDomain.getNickname(),
            preSignedUrl.preSignedUrl(),
            userDomain.getLeave(),
            userDomain.getGender(),
            userDomain.getBirthdate());
    log.info("updatedUserInfo: {}", userResponseDto);
    return userResponseDto;
  }

  private UserDomain findUserById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
  }

  private void validateName(String name) {
    if (isExceedingMaxLength(name, 15)) {
      throw new IllegalArgumentException("The name exceeds the maximum length of 15 characters.");
    }
  }

  private void validateUsername(String username) {
    if (!username.matches("^[a-zA-Z0-9]+$")) {
      throw new IllegalArgumentException("Username must contain only letters and numbers.");
    }
    if (containsBadWord(username)) {
      throw new IllegalArgumentException("The username contains words that are not allowed.");
    }
    if (isExceedingMaxLength(username, 20)) {
      throw new IllegalArgumentException(
          "The username exceeds the maximum length of 20 characters.");
    }

    boolean usernameExists = userRepository.existsByUsername(username);
    if (usernameExists) {
      throw new IllegalArgumentException("Username is already taken.");
    }
  }

  private void validateNickname(String nickname) {
    if (!nickname.matches("^[a-zA-Z0-9]+$")) {
      throw new IllegalArgumentException("Nickname must contain only letters and numbers.");
    }
    if (containsBadWord(nickname)) {
      throw new IllegalArgumentException("The nickname contains words that are not allowed.");
    }
    if (isExceedingMaxLength(nickname, 15)) {
      throw new IllegalArgumentException(
          "The nickname exceeds the maximum length of 15 characters.");
    }
  }

  private boolean containsBadWord(String value) {
    BadWordFiltering badWordFiltering = new BadWordFiltering();
    return badWordFiltering.check(value);
  }

  private boolean isExceedingMaxLength(String value, int maxLength) {
    return value.length() > maxLength;
  }
}
