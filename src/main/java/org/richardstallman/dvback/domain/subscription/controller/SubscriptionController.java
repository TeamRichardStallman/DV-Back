package org.richardstallman.dvback.domain.subscription.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.subscription.domain.request.SubscriptionCreateRequestDto;
import org.richardstallman.dvback.domain.subscription.domain.response.SubscriptionResponseDto;
import org.richardstallman.dvback.domain.subscription.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubscriptionController {
  private final SubscriptionService subscriptionService;

  @PostMapping
  public ResponseEntity<DvApiResponse<SubscriptionResponseDto>> createSubscription(
      @Valid @RequestBody final SubscriptionCreateRequestDto subscriptionCreateRequestDto,
      @AuthenticationPrincipal final Long userId) {
    final SubscriptionResponseDto subscriptionResponseDto =
        subscriptionService.createSubscription(subscriptionCreateRequestDto, userId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(DvApiResponse.of(subscriptionResponseDto));
  }

  @GetMapping("/user")
  public ResponseEntity<DvApiResponse<List<SubscriptionResponseDto>>> getSubscriptionsByUser(
      @AuthenticationPrincipal final Long userId) {
    final List<SubscriptionResponseDto> subscriptions =
        subscriptionService.getSubscriptionsByUserId(userId);
    return ResponseEntity.ok(DvApiResponse.of(subscriptions));
  }

  @DeleteMapping("/{subscriptionId}")
  public ResponseEntity<DvApiResponse<SubscriptionResponseDto>> deactivateSubscription(
      @PathVariable final Long subscriptionId, @AuthenticationPrincipal final Long userId) {
    subscriptionService.deleteSubscription(subscriptionId, userId);
    return ResponseEntity.noContent().build();
  }
}
