package org.richardstallman.dvback.global.oauth.service;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

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

  @Value("${app.properties.secure}")
  private Boolean secure;

  public ResponseCookie createCookie(String cookieName, String cookieValue) {
    return ResponseCookie.from(cookieName, cookieValue)
        .domain(mainDomain)
        .path("/")
        .secure(secure)
        .httpOnly(true)
        .sameSite(sameSite)
        .build();
  }

  public void deleteCookie(HttpServletResponse httpServletResponse, String cookieName) {
    ResponseCookie deleteCookie =
        ResponseCookie.from(cookieName, "")
            .domain(mainDomain)
            .path("/")
            .secure(secure)
            .httpOnly(true)
            .maxAge(0)
            .sameSite(sameSite)
            .maxAge(0)
            .build();
    log.info("deleteCookie : {}", deleteCookie);
    httpServletResponse.addHeader(SET_COOKIE, deleteCookie.toString());
  }
}
