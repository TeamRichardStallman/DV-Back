package org.richardstallman.dvback.domain.user.service;

import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;

public interface UserService {

  UserResponseDto updateUserInfo(Long userId, UserRequestDto userRequestDto);

  UserResponseDto getUserInfo(Long userId);

  UserResponseDto deactivateUser(Long userId);
}
