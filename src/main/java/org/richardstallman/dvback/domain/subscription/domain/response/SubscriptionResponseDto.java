package org.richardstallman.dvback.domain.subscription.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SubscriptionResponseDto(
    @NotNull Long subscriptionId,
    @NotNull Long userId,
    @NotNull Long jobId,
    @NotNull LocalDateTime subscribedAt) {}
