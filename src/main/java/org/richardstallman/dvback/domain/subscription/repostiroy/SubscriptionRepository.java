package org.richardstallman.dvback.domain.subscription.repostiroy;

import java.util.List;
import java.util.Optional;
import org.richardstallman.dvback.domain.subscription.domain.SubscriptionDomain;

public interface SubscriptionRepository {
  SubscriptionDomain save(SubscriptionDomain subscriptionDomain);

  List<SubscriptionDomain> findByUserId(Long userId);

  Optional<SubscriptionDomain> findBySubscriptionIdAndUserId(Long subscriptionId, Long userId);

  void deleteById(Long subscriptionId);

  boolean existsByUserIdAndJobId(Long userId, Long jobId);
}
