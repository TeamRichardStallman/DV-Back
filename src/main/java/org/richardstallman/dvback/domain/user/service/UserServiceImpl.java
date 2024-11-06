package org.richardstallman.dvback.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserConverter userConverter;

  @Transactional
  public UserResponseDto updateUserInfo(Long userId, UserRequestDto userRequestDto) {
    UserDomain user = findUserById(userId);
    UserEntity userEntity = userConverter.fromDomainToEntity(user);

    UserEntity updatedUser =
        userEntity.updatedUserEntity(
            userRequestDto.nickname(), userRequestDto.birthdate(), userRequestDto.gender());

    userRepository.save(userConverter.fromEntityToDomain(updatedUser));

    return new UserResponseDto(
        updatedUser.getId(),
        updatedUser.getSocialId(),
        updatedUser.getEmail(),
        updatedUser.getName(),
        updatedUser.getNickname(),
        updatedUser.getS3ProfileImageUrl(),
        updatedUser.getLeave(),
        updatedUser.getGender(),
        updatedUser.getBirthdate());
  }

  @Override
  public UserResponseDto getUserInfo(Long userId) {
    log.info("getUserInfo");
    UserDomain userDomain = findUserById(userId);
    return userConverter.fromDomainToDto(userDomain);
  }

  @Override
  public UserResponseDto deactivateUser(Long userId) {
    UserDomain user = findUserById(userId);
    UserEntity userEntity = userConverter.fromDomainToEntity(user);
    UserEntity deactivatedUser = userEntity.deactivate();
    userRepository.save(userConverter.fromEntityToDomain(deactivatedUser));

    return new UserResponseDto(
        deactivatedUser.getId(),
        deactivatedUser.getSocialId(),
        deactivatedUser.getEmail(),
        deactivatedUser.getName(),
        deactivatedUser.getNickname(),
        deactivatedUser.getS3ProfileImageUrl(),
        deactivatedUser.getLeave(),
        deactivatedUser.getGender(),
        deactivatedUser.getBirthdate());
  }

  private UserDomain findUserById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
  }
}
