package org.richardstallman.dvback.domain.subscription.repostiroy;

import java.util.List;
import java.util.Optional;
import org.richardstallman.dvback.domain.subscription.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, Long> {
  List<SubscriptionEntity> findByUserUserIdOrderBySubscriptionIdDesc(Long userId);

  Optional<SubscriptionEntity> findBySubscriptionIdAndUserUserId(Long subscriptionId, Long userId);

  boolean existsByUserUserIdAndJobJobId(Long userId, Long jobId);
}
