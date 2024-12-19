package org.richardstallman.dvback.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.ResponseCode;

@Data
@NoArgsConstructor
public class DvApiResponse<T> {

  private int code;
  private String message;
  private T data;

  public static <T> DvApiResponse<T> of(ResponseCode responseCode, T data) {
    return new DvApiResponse<T>(responseCode, data);
  }

  public static <T> DvApiResponse<T> of(ResponseCode responseCode, String message, T data) {
    return new DvApiResponse<T>(responseCode, data, message);
  }

  public static <T> DvApiResponse<T> of(ResponseCode responseCode, String message) {
    return new DvApiResponse<T>(responseCode, null, message);
  }

  public static <T> DvApiResponse<T> of(T data) {
    return new DvApiResponse<T>(ResponseCode.SUCCESS, data);
  }

  private DvApiResponse(ResponseCode responseCode, T data) {
    this.code = responseCode.getCode();
    this.message = responseCode.getMessage();
    this.data = data;
  }

  private DvApiResponse(ResponseCode responseCode, T data, String message) {
    this.code = responseCode.getCode();
    this.message = message;
    this.data = data;
  }
}
