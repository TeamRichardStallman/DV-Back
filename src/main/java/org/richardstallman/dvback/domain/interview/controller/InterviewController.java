package org.richardstallman.dvback.domain.interview.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewAddFileRequestDto;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewAddFileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationListResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewListResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/interview", produces = MediaType.APPLICATION_JSON_VALUE)
public class InterviewController {

  private final InterviewService interviewService;

  @PostMapping
  public ResponseEntity<DvApiResponse<InterviewCreateResponseDto>> createInterview(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody final InterviewCreateRequestDto interviewCreateRequestDto) {

    log.info("Authenticated user ID: {}", userId);

    final InterviewCreateResponseDto interviewCreateResponseDto =
        interviewService.createInterview(interviewCreateRequestDto, userId);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(DvApiResponse.of(interviewCreateResponseDto));
  }

  @PutMapping
  public ResponseEntity<DvApiResponse<InterviewAddFileResponseDto>> addInterviewFile(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody final InterviewAddFileRequestDto interviewAddFileRequestDto) {
    log.info(
        "Authenticated user ID When Add Interview File: {} {}", userId, interviewAddFileRequestDto);

    final InterviewAddFileResponseDto interviewAddFileResponseDto =
        interviewService.addInterviewFile(interviewAddFileRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(interviewAddFileResponseDto));
  }

  @GetMapping("/evaluation")
  public ResponseEntity<DvApiResponse<InterviewEvaluationListResponseDto>>
      getInterviewsForEvaluation(@AuthenticationPrincipal final Long userId) {
    final InterviewEvaluationListResponseDto interviewEvaluationListResponseDto =
        interviewService.getInterviewsByUserIdForEvaluation(userId);
    return ResponseEntity.ok(DvApiResponse.of(interviewEvaluationListResponseDto));
  }

  @GetMapping
  public ResponseEntity<DvApiResponse<InterviewListResponseDto>> getInterviews(
      @AuthenticationPrincipal final Long userId) {
    final InterviewListResponseDto interviewListResponseDto =
        interviewService.getInterviewsByUserId(userId);
    return ResponseEntity.ok(DvApiResponse.of(interviewListResponseDto));
  }

  @GetMapping("/{interviewId}/owner")
  public ResponseEntity<DvApiResponse<Boolean>> checkInterviewOwner(
      @AuthenticationPrincipal final Long userId, @PathVariable final Long interviewId) {
    boolean isOwner = interviewService.checkInterviewOwner(userId, interviewId);
    return ResponseEntity.ok(DvApiResponse.of(isOwner));
  }
}
