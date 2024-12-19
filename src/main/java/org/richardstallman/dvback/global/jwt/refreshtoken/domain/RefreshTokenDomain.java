package org.richardstallman.dvback.global.jwt.refreshtoken.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefreshTokenDomain {

  private final String token;
  private final Long userId;
}
