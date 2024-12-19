package org.richardstallman.dvback.domain.subscription.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Builder
@Getter
public class SubscriptionDomain {
  private final Long subscriptionId;
  private final UserDomain user;
  private JobDomain job;
  private LocalDateTime subscribedAt;
}
