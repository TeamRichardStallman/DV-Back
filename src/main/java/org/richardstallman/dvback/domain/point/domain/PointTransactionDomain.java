package org.richardstallman.dvback.domain.point.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.PointTransactionType;

@Builder
@Getter
public class PointTransactionDomain {

  private final Long pointTransactionId;
  private Long userId;
  private int amount;
  private PointTransactionType pointTransactionType;
  private String description;
}
