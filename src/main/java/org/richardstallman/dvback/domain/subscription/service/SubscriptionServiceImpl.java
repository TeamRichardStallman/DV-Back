package org.richardstallman.dvback.domain.subscription.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.domain.subscription.converter.SubscriptionConverter;
import org.richardstallman.dvback.domain.subscription.domain.SubscriptionDomain;
import org.richardstallman.dvback.domain.subscription.domain.request.SubscriptionCreateRequestDto;
import org.richardstallman.dvback.domain.subscription.domain.response.SubscriptionResponseDto;
import org.richardstallman.dvback.domain.subscription.repostiroy.SubscriptionRepository;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionConverter subscriptionConverter;
  private final UserRepository userRepository;
  private final JobService jobService;

  @Override
  public SubscriptionResponseDto createSubscription(
      SubscriptionCreateRequestDto requestDto, Long userId) {

    if (subscriptionRepository.existsByUserIdAndJobId(userId, requestDto.jobId())) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST,
          String.format("User %d has already subscribed to job %d", userId, requestDto.jobId()));
    }

    UserDomain userDomain =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    JobDomain jobDomain = jobService.findJobById(requestDto.jobId());
    LocalDateTime subscribedAt = LocalDateTime.now();
    SubscriptionDomain subscriptionDomain =
        subscriptionConverter.createDomain(userDomain, jobDomain, subscribedAt);
    subscriptionDomain = subscriptionRepository.save(subscriptionDomain);
    return subscriptionConverter.fromDomainToResponseDto(subscriptionDomain);
  }

  @Override
  public List<SubscriptionResponseDto> getSubscriptionsByUserId(Long userId) {
    List<SubscriptionDomain> subscriptions = subscriptionRepository.findByUserId(userId);

    return subscriptions.stream().map(subscriptionConverter::fromDomainToResponseDto).toList();
  }

  @Override
  public void deleteSubscription(Long jobId, Long userId) {
    SubscriptionDomain subscriptionDomain =
        subscriptionRepository
            .findByJobIdAndUserId(jobId, userId)
            .orElseThrow(
                () ->
                    new ApiException(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            "Subscription with id %d for user %d not found", jobId, userId)));

    subscriptionRepository.deleteById(subscriptionDomain.getSubscriptionId());
  }
}
