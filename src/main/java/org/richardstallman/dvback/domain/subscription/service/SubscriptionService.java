package org.richardstallman.dvback.domain.subscription.service;

import java.util.List;
import org.richardstallman.dvback.domain.subscription.domain.request.SubscriptionCreateRequestDto;
import org.richardstallman.dvback.domain.subscription.domain.response.SubscriptionResponseDto;

public interface SubscriptionService {
  SubscriptionResponseDto createSubscription(SubscriptionCreateRequestDto requestDto, Long userId);

  List<SubscriptionResponseDto> getSubscriptionsByUserId(Long userId);

  void deleteSubscription(Long subscriptionId, Long userId);
}
