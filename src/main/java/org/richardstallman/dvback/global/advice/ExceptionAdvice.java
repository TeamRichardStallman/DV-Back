package org.richardstallman.dvback.global.advice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.common.constant.CommonConstants.ResponseCode;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(ServerWebInputException.class)
  @ResponseStatus(BAD_REQUEST)
  public ResponseEntity<DvApiResponse<String>> handleBadRequestException(Exception ex) {
    log.error("Bad Request : ({})", ex.getMessage());
    return ResponseEntity.status(BAD_REQUEST)
        .body(DvApiResponse.of(CommonConstants.ResponseCode.FAIL, ex.getMessage()));
  }

  @ExceptionHandler(ApiException.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ResponseEntity<DvApiResponse<String>> handleApiException(ApiException ex) {
    log.error("Internal Server Error - Runtime Exception : ({})", ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus())
        .body(
            DvApiResponse.of(
                ResponseCode.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.name(), ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ResponseEntity<?> handleException(Exception ex) {
    String stackTrace = Arrays.toString(ex.getStackTrace());
    log.error("An exception has occurred : ({})", ex.getMessage() + "\n" + stackTrace);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(
            DvApiResponse.of(
                ResponseCode.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.name(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(BAD_REQUEST)
  public ResponseEntity<DvApiResponse<String>> handleValidationException(
      MethodArgumentNotValidException ex) {
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .findFirst()
            .orElse("Invalid input");

    log.error("Validation error: {}", errorMessage);
    return ResponseEntity.status(BAD_REQUEST)
        .body(
            DvApiResponse.of(CommonConstants.ResponseCode.FAIL, BAD_REQUEST.name(), errorMessage));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(BAD_REQUEST)
  public ResponseEntity<DvApiResponse<String>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    log.error("Request body is missing or unreadable: {}", ex.getMessage());
    return ResponseEntity.status(BAD_REQUEST)
        .body(
            DvApiResponse.of(
                CommonConstants.ResponseCode.FAIL,
                BAD_REQUEST.name(),
                "Request body is missing or unreadable"));
  }
}
