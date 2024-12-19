package org.richardstallman.dvback.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.global.jwt.domain.response.JwtClaimResponseDto;
import org.richardstallman.dvback.global.oauth.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  @Value("#{T(java.util.Arrays).asList('${security.excluded-paths}'.split(','))}")
  private List<String> excludedPaths;

  private final JwtUtil jwtUtil;
  private final TokenService tokenService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String requestPath = request.getRequestURI();
    log.info("Request path: {}", requestPath);

    return excludedPaths.stream().anyMatch(excludedPath -> matchPath(requestPath, excludedPath));
  }

  private boolean matchPath(String requestPath, String excludedPath) {
    if (excludedPath.endsWith("/**")) {
      String prefix = excludedPath.substring(0, excludedPath.length() - 2);
      return requestPath.startsWith(prefix);
    }
    return requestPath.equals(excludedPath);
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String clientIp = getClientIp(request);
    log.info("Incoming request from client IP: {}", clientIp);

    Cookie[] cookies = request.getCookies();
    if (cookies == null || !existAccessToken(cookies)) {
      log.info("cookie is null");
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = extractCookie(cookies, JwtUtil.ACCESS_TOKEN);
    log.info("doFilterInternal - accessToken : {}", accessToken);
    String refreshToken = extractCookie(cookies, JwtUtil.REFRESH_TOKEN);
    log.info("doFilterInternal - refreshToken : {}", refreshToken);

    if (accessToken == null || jwtUtil.validateToken(accessToken)) {
      log.info("Access token is missing or invalid, attempting to renew with refresh token.");
      if (refreshToken != null && !jwtUtil.validateToken(refreshToken)) {
        refreshToken = tokenService.renewRefreshToken(response, refreshToken);
        accessToken = tokenService.renewAccessToken(response, refreshToken);
        log.info("Access token renewed successfully.");
      } else {
        log.warn("Refresh token is missing or invalid.");
        writeErrorResponse(response);
        return;
      }
    }

    log.info("Setting authentication in security context.");

    setAuthInSecurityContext(accessToken);
    filterChain.doFilter(request, response);
  }

  private void writeErrorResponse(HttpServletResponse response) throws IOException {
    response.setStatus(403);
    response.setContentType("application/json; charset=utf-8");
  }

  private void setAuthInSecurityContext(String accessToken) {
    JwtClaimResponseDto jwtClaimResponseDto = jwtUtil.extractClaims(accessToken);
    if (jwtClaimResponseDto == null) {
      log.warn("Failed to extract claims from token.");
      return;
    }

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(jwtClaimResponseDto.getUserId(), null, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private String extractCookie(Cookie[] cookies, String cookieName) {
    return Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(cookieName))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }

  private boolean existAccessToken(Cookie[] authCookies) {
    return Arrays.stream(authCookies).anyMatch(name -> name.getName().equals("access_token"));
  }
}
