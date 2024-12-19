package org.richardstallman.dvback.global.advice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private HttpStatus httpStatus;
  private String message;

  public ApiException() {
    super();
  }

  public ApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApiException(String message) {
    super(message);
  }

  public ApiException(Throwable cause) {
    super(cause);
  }

  public ApiException(HttpStatus httpStatus, String message) {
    super();
    this.httpStatus = httpStatus;
    this.message = message;
  }
}
