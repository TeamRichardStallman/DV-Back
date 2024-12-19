package org.richardstallman.dvback.global.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    log.info("request: {}", request);
    log.info("response: {}", response);
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.getWriter().write("Kakao login failed! Please check the server logs");

    // 에러 메시지와 스택 트레이스 로그 기록
    log.error("Kakao login failed. Error message: {}", exception.getMessage(), exception);
  }
}
