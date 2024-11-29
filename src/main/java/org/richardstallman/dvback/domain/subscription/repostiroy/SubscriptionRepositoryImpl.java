package org.richardstallman.dvback.domain.subscription.repostiroy;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.subscription.converter.SubscriptionConverter;
import org.richardstallman.dvback.domain.subscription.domain.SubscriptionDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

  private final SubscriptionJpaRepository subscriptionJpaRepository;
  private final SubscriptionConverter subscriptionConverter;

  @Override
  public SubscriptionDomain save(SubscriptionDomain subscriptionDomain) {
    return subscriptionConverter.fromEntityToDomain(
        subscriptionJpaRepository.save(
            subscriptionConverter.fromDomainToEntity(subscriptionDomain)));
  }

  @Override
  public List<SubscriptionDomain> findByUserId(Long userId) {
    return subscriptionJpaRepository.findByUserIdOrderBySubscriptionIdDesc(userId).stream()
        .map(subscriptionConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public Optional<SubscriptionDomain> findBySubscriptionIdAndUserId(
      Long subscriptionId, Long userId) {
    return subscriptionJpaRepository
        .findBySubscriptionIdAndUserId(subscriptionId, userId)
        .map(subscriptionConverter::fromEntityToDomain);
  }

  @Override
  public void deleteById(Long subscriptionId) {
    subscriptionJpaRepository.deleteById(subscriptionId);
  }

  @Override
  public boolean existsByUserIdAndJobId(Long userId, Long jobId) {
    return subscriptionJpaRepository.existsByUserIdAndJobJobId(userId, jobId);
  }
}