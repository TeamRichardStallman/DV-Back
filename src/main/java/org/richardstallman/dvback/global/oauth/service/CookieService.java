package org.richardstallman.dvback.global.oauth.service;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CookieService {

  @Value("${app.properties.mainDomain}")
  private String mainDomain;

  @Value("${app.properties.cookie.sameSite}")
  private String sameSite;

  public ResponseCookie createCookie(String cookieName, String cookieValue) {
    return ResponseCookie.from(cookieName, cookieValue)
        .domain(mainDomain)
        .path("/")
        .secure(true)
        .httpOnly(true)
        .sameSite(sameSite)
        .build();
  }

  public void createExpiredCookie(HttpServletResponse response, String cookieName) {
    Cookie cookie = new Cookie(cookieName, null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0); // 즉시 만료
    response.addCookie(cookie);
  }

  public void deleteCookie(HttpServletResponse httpServletResponse, String cookieName) {
    ResponseCookie deleteCookie =
        ResponseCookie.from(cookieName, "")
            .path("/")
            .httpOnly(true)
            .maxAge(0)
            .sameSite(sameSite)
            .secure(true)
            .build();

    httpServletResponse.addHeader(SET_COOKIE, deleteCookie.toString());
  }
}