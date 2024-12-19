package org.richardstallman.dvback.domain.subscription.converter;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.subscription.domain.SubscriptionDomain;
import org.richardstallman.dvback.domain.subscription.domain.response.SubscriptionResponseDto;
import org.richardstallman.dvback.domain.subscription.entity.SubscriptionEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionConverter {
  private final UserConverter userConverter;
  private final JobConverter jobConverter;

  public SubscriptionDomain fromEntityToDomain(SubscriptionEntity subscriptionEntity) {
    return SubscriptionDomain.builder()
        .subscriptionId(subscriptionEntity.getSubscriptionId())
        .user(userConverter.fromEntityToDomain(subscriptionEntity.getUser()))
        .job(jobConverter.toDomain(subscriptionEntity.getJob()))
        .subscribedAt(subscriptionEntity.getSubscribedAt())
        .build();
  }

  public SubscriptionEntity fromDomainToEntity(SubscriptionDomain subscriptionDomain) {
    return new SubscriptionEntity(
        subscriptionDomain.getSubscriptionId(),
        userConverter.fromDomainToEntity(subscriptionDomain.getUser()),
        jobConverter.toEntity(subscriptionDomain.getJob()),
        subscriptionDomain.getSubscribedAt());
  }

  public SubscriptionDomain createDomain(
      UserDomain userDomain, JobDomain jobDomain, LocalDateTime subscribedAt) {
    return SubscriptionDomain.builder()
        .user(userDomain)
        .job(jobDomain)
        .subscribedAt(subscribedAt)
        .build();
  }

  public SubscriptionResponseDto fromDomainToResponseDto(SubscriptionDomain subscriptionDomain) {
    return new SubscriptionResponseDto(
        subscriptionDomain.getSubscriptionId(),
        subscriptionDomain.getUser().getUserId(),
        subscriptionDomain.getJob().getJobId(),
        subscriptionDomain.getSubscribedAt());
  }
}
