package org.richardstallman.dvback.domain.evaluation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants.ResponseCode;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.service.EvaluationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/evaluation", produces = MediaType.APPLICATION_JSON_VALUE)
public class EvaluationController {

  private final EvaluationService evaluationService;

  @PostMapping
  public ResponseEntity<DvApiResponse<?>> requestOverallEvaluation(
      @AuthenticationPrincipal final Long userId,
      @Valid @RequestBody final OverallEvaluationRequestDto overallEvaluationRequestDto) {
    log.info("Received evaluation request for user: ({})", userId);
    evaluationService.getOverallEvaluation(overallEvaluationRequestDto, userId);
    return ResponseEntity.accepted()
        .body(
            DvApiResponse.of(
                ResponseCode.ACCEPTED,
                "Request sent successfully!",
                String.format(
                    "interview id for tracking: (%d)", overallEvaluationRequestDto.interviewId())));
  }

  @GetMapping("/{interviewId}")
  public ResponseEntity<DvApiResponse<OverallEvaluationResponseDto>>
      getOverallEvaluationByInterviewId(@Valid @PathVariable final Long interviewId) {
    log.info("Received evaluation request for user: ({})", interviewId);
    final OverallEvaluationResponseDto overallEvaluationResponseDto =
        evaluationService.getOverallEvaluationByInterviewId(interviewId);
    log.info("Returning overall evaluation response: {}", overallEvaluationResponseDto);
    return ResponseEntity.ok(DvApiResponse.of(overallEvaluationResponseDto));
  }

  @PostMapping("/completion")
  public ResponseEntity<DvApiResponse<?>> saveEvaluationResults(
      @RequestBody @Valid OverallEvaluationResultRequestDto evaluationResult) {
    evaluationService.saveCompletedEvaluation(evaluationResult);
    return ResponseEntity.ok(
        DvApiResponse.of(ResponseCode.SUCCESS, "Evaluations have been successfully saved."));
  }
}
