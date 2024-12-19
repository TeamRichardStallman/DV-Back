package org.richardstallman.dvback.global.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class TimeUtil {

  public static LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }

  public static LocalDateTime generateExpirationDateTime(LocalDateTime now) {
    return now.plusMonths(1).with(LocalTime.MAX);
  }
}
