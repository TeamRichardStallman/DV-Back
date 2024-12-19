package org.richardstallman.dvback.global.oauth.service;

import static org.richardstallman.dvback.global.jwt.JwtUtil.ACCESS_TOKEN;
import static org.richardstallman.dvback.global.jwt.JwtUtil.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.jwt.refreshtoken.entity.RefreshTokenEntity;
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final CookieService cookieService;
  private final UserConverter userConverter;

  public String renewAccessToken(HttpServletResponse httpServletResponse, String refreshToken) {
    String newAccessToken = createAccessToken(refreshToken);
    ResponseCookie accessCookie = cookieService.createCookie(ACCESS_TOKEN, newAccessToken);
    httpServletResponse.addHeader(SET_COOKIE, accessCookie.toString());
    return newAccessToken;
  }

  public String renewRefreshToken(HttpServletResponse httpServletResponse, String refreshToken) {
    RefreshTokenEntity storedRefreshToken =
        refreshTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(() -> new EntityNotFoundException("Refresh Token not found."));
    Long userId = storedRefreshToken.getUserId();
    deleteRefreshTokenByRefreshToken(storedRefreshToken.getRefreshToken());
    String newRefreshToken = jwtUtil.generateRefreshToken(userId);
    log.info("Refresh Token: {}", newRefreshToken);
    saveRefreshToken(newRefreshToken, userId);
    ResponseCookie refreshCookie = cookieService.createCookie(REFRESH_TOKEN, newRefreshToken);
    httpServletResponse.addHeader(SET_COOKIE, refreshCookie.toString());
    return newRefreshToken;
  }

  public String createAccessToken(String refreshToken) {
    if (jwtUtil.validateToken(refreshToken)) {
      throw new IllegalArgumentException("Invalid or expired refresh token.");
    }
    RefreshTokenEntity storedRefreshToken =
        refreshTokenRepository
            .findByRefreshToken(refreshToken)
            .orElseThrow(() -> new EntityNotFoundException("Refresh Token not found."));
    UserDomain userDomain =
        userRepository
            .findById(storedRefreshToken.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found."));

    UserEntity userEntity = userConverter.fromDomainToEntity(userDomain);
    return jwtUtil.generateAccessToken(userEntity.getUserId());
  }

  public void saveRefreshToken(String refreshToken, Long userId) {
    log.info("saveRefreshToken - refreshToken : {}", refreshToken);
    refreshTokenRepository.save(new RefreshTokenEntity(refreshToken, userId));
  }

  public void deleteRefreshTokenByUserId(Long userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

  public void deleteRefreshTokenByRefreshToken(String refreshToken) {
    if (refreshTokenRepository.existsById(refreshToken)) {
      refreshTokenRepository.deleteById(refreshToken);
    }
  }

  public String getTokenFromCookies(Cookie[] cookies, String tokenName) {
    if (cookies == null) {
      log.info("cookie is null");
      return null;
    }
    for (Cookie cookie : cookies) {
      if (tokenName.equals(cookie.getName())) {
        log.info("cookie: " + cookie.getValue());
        return cookie.getValue();
      }
    }
    return null;
  }
}
