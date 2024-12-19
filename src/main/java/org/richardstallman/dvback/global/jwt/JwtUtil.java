package org.richardstallman.dvback.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.global.jwt.domain.response.JwtClaimResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
  private static final String USER_ID = "userId";
  public static final String ACCESS_TOKEN = "access_token";
  public static final String REFRESH_TOKEN = "refresh_token";

  @Value("${app.jwt.access-expiration}")
  private long accessTokenExpiration;

  @Value("${app.jwt.refresh-expiration}")
  private long refreshTokenExpiration;

  @Value("${app.jwt.secret-key}")
  private String secretCode;

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    secretKey = Keys.hmacShaKeyFor(secretCode.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(Long userId) {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + accessTokenExpiration);

    return Jwts.builder()
        .subject(ACCESS_TOKEN)
        .claim(USER_ID, userId)
        .expiration(expireDate)
        .signWith(secretKey, SIG.HS512)
        .compact();
  }

  public String generateRefreshToken(Long userId) {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + refreshTokenExpiration);

    return Jwts.builder()
        .subject(REFRESH_TOKEN)
        .claim(USER_ID, userId)
        .expiration(expireDate)
        .signWith(secretKey, SIG.HS512)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
      return false;
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token.");
    } catch (SecurityException | MalformedJwtException e) {
      log.error("Invalid JWT signature.");
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported v token.");
    } catch (IllegalArgumentException e) {
      log.error("Incorrect JWT Token.");
    }
    return true;
  }

  public JwtClaimResponseDto extractClaims(String token) {
    Claims payload =
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

    Long userId = payload.get(USER_ID, Long.class);

    return new JwtClaimResponseDto(userId);
  }

  public boolean isExpired(String token) {
    try {
      Date expiration =
          Jwts.parser()
              .verifyWith(secretKey)
              .build()
              .parseSignedClaims(token)
              .getPayload()
              .getExpiration();
      return expiration.before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }
}
