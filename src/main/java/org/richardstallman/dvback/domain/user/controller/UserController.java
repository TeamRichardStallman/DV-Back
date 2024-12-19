package org.richardstallman.dvback.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;
import org.richardstallman.dvback.domain.ticket.service.TicketService;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.request.UserUpdateRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserLoginResponseDto;
import org.richardstallman.dvback.domain.user.domain.response.UserMyPageResponseDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

  private final TokenService tokenService;
  private final UserService userService;
  private final TicketService ticketService;
  private final UserConverter userConverter;
  private final JwtUtil jwtUtil;

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken =
        tokenService.getTokenFromCookies(request.getCookies(), JwtUtil.REFRESH_TOKEN);
    userService.logout(response, refreshToken);

    return ResponseEntity.ok("Logged out successfully");
  }

  @PostMapping(value = "/info")
  public ResponseEntity<DvApiResponse<UserResponseDto>> createUserInfo(
      @AuthenticationPrincipal Long userId,
      @RequestBody @Valid final UserRequestDto userRequestDto) {
    final UserResponseDto userResponseDto = userService.createUserInfo(userId, userRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }

  @PutMapping(value = "/info")
  public ResponseEntity<DvApiResponse<UserResponseDto>> updateUserInfo(
      @AuthenticationPrincipal Long userId,
      @RequestBody @Valid final UserUpdateRequestDto userUpdateRequestDto) {
    final UserResponseDto userResponseDto =
        userService.updateUserInfo(userId, userUpdateRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }

  @GetMapping("/info")
  public ResponseEntity<DvApiResponse<UserResponseDto>> getUserInfo(
      @AuthenticationPrincipal Long userId) {
    final UserResponseDto userResponseDto = userService.getUserInfo(userId);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }

  @GetMapping("/login")
  public ResponseEntity<DvApiResponse<UserLoginResponseDto>> login(
      @AuthenticationPrincipal Long userId) {
    final UserResponseDto userResponseDto = userService.getUserInfo(userId);
    if (userResponseDto.gender() == null) {
      return ResponseEntity.ok(
          DvApiResponse.of(userConverter.forSignUp(userResponseDto, "signup")));
    }
    return ResponseEntity.ok(
        DvApiResponse.of(
            userConverter.fromResponseDtoToLoginResponseDto(userResponseDto, "login")));
  }

  @GetMapping("/authenticated")
  public ResponseEntity<DvApiResponse<Boolean>> isAuthenticated(HttpServletRequest request) {
    String accessToken =
        tokenService.getTokenFromCookies(request.getCookies(), JwtUtil.ACCESS_TOKEN);

    if (accessToken == null || accessToken.isEmpty()) {
      return ResponseEntity.ok(DvApiResponse.of(false));
    }

    if (jwtUtil.validateToken(accessToken)) {
      return ResponseEntity.ok(DvApiResponse.of(false));
    }

    return ResponseEntity.ok(DvApiResponse.of(true));
  }

  @GetMapping("/validate-username")
  public ResponseEntity<DvApiResponse<Boolean>> validateUsername(
      @RequestParam("username") String username) {

    if (userService.existsByUsername(username)) {
      return ResponseEntity.ok(DvApiResponse.of(true));
    }

    return ResponseEntity.ok(DvApiResponse.of(false));
  }

  @GetMapping("/my-page")
  public ResponseEntity<DvApiResponse<UserMyPageResponseDto>> getMyPageUserInfo(
      @AuthenticationPrincipal Long userId) {
    final UserResponseDto userResponseDto = userService.getUserInfo(userId);
    final TicketUserCountInfoDto ticketUserCountInfoDto = ticketService.getUserCountInfo(userId);
    return ResponseEntity.ok(
        DvApiResponse.of(new UserMyPageResponseDto(userResponseDto, ticketUserCountInfoDto)));
  }
}
