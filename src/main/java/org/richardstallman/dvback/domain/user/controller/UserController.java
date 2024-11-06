package org.richardstallman.dvback.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.richardstallman.dvback.global.oauth.service.CookieService;
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
  private final CookieService cookieService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserService userService;

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    cookieService.createExpiredCookie(response, JwtUtil.ACCESS_TOKEN);
    cookieService.createExpiredCookie(response, JwtUtil.REFRESH_TOKEN);

    String refreshToken =
        tokenService.getTokenFromCookies(request.getCookies(), JwtUtil.REFRESH_TOKEN);
    if (refreshToken != null) {
      refreshTokenRepository.deleteById(refreshToken);
    }

    return ResponseEntity.ok("Logged out successfully");
  }

  @PutMapping("/info")
  public ResponseEntity<DvApiResponse<UserResponseDto>> updateUserInfo(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody final UserRequestDto userRequestDto) {
    final UserResponseDto userResponseDto = userService.updateUserInfo(userId, userRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }

  @GetMapping("/info")
  public ResponseEntity<DvApiResponse<UserResponseDto>> getUserInfo(
      @AuthenticationPrincipal Long userId) {
    final UserResponseDto userResponseDto = userService.getUserInfo(userId);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }

  @DeleteMapping("/leave")
  public ResponseEntity<DvApiResponse<UserResponseDto>> deactivateUser(
      @AuthenticationPrincipal Long userId) {
    UserResponseDto userResponseDto = userService.deactivateUser(userId);
    return ResponseEntity.ok(DvApiResponse.of(userResponseDto));
  }
}
