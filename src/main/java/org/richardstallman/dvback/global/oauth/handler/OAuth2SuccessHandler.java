package org.richardstallman.dvback.global.oauth.handler;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.oauth.OAuth2CustomUser;
import org.richardstallman.dvback.global.oauth.service.CookieService;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Value("${app.properties.frontendDomain}")
  private String frontendDomain;

  private final JwtUtil jwtUtil;
  private final CookieService cookieService;
  private final TokenService tokenService;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    log.info("OAuth2 인증 성공 (OAuth2SuccessHandler)");
    try {
      OAuth2CustomUser oAuth2User = (OAuth2CustomUser) authentication.getPrincipal();
      log.info("onAuthenticationSuccess - userId: " + oAuth2User.getUserId());
      String accessToken = jwtUtil.generateAccessToken(oAuth2User.getUserId());
      String refreshToken = jwtUtil.generateRefreshToken(oAuth2User.getUserId());
      log.info("accessToken: " + accessToken);
      log.info("refreshToken: " + refreshToken);

      tokenService.saveRefreshToken(refreshToken, oAuth2User.getUserId());
      log.info("Redis에 refreshToken 저장 완료");

      ResponseCookie accessTokenCookie =
          cookieService.createCookie(JwtUtil.ACCESS_TOKEN, accessToken);
      ResponseCookie refreshTokenCookie =
          cookieService.createCookie(JwtUtil.REFRESH_TOKEN, refreshToken);

      response.addHeader(SET_COOKIE, accessTokenCookie.toString());
      response.addHeader(SET_COOKIE, refreshTokenCookie.toString());

      log.info("frontendDomain: " + frontendDomain);
      response.sendRedirect(frontendDomain + "/auth");
    } catch (Exception e) {
      log.error("OAuth2 Login 성공 후 예외 발생 : {}", e.getMessage());
    }
  }
}
