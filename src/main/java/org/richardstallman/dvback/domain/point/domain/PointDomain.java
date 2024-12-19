package org.richardstallman.dvback.domain.point.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PointDomain {

  private final Long pointId;
  private final Long userId;
  private int balance;
}
