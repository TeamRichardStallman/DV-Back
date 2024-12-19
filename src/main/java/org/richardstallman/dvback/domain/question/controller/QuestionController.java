package org.richardstallman.dvback.domain.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants.ResponseCode;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionNextRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionRequestListDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionResultRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;
import org.richardstallman.dvback.domain.question.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/question")
public class QuestionController {

  private final QuestionService questionService;

  @PostMapping
  public ResponseEntity<DvApiResponse<?>> requestQuestionList(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody final QuestionRequestListDto questionRequestListDto) {
    log.info("Received request to request question list for user: ({})", userId);
    questionService.getQuestionList(questionRequestListDto, userId);
    return ResponseEntity.accepted()
        .body(
            DvApiResponse.of(
                ResponseCode.ACCEPTED,
                "Request sent successfully!",
                String.format(
                    "interview id for tracking: (%d)", questionRequestListDto.interviewId())));
  }

  @PostMapping("/initial-question")
  public ResponseEntity<DvApiResponse<QuestionResponseDto>> getInitialQuestion(
      @AuthenticationPrincipal Long userId,
      @Validated @RequestBody final QuestionInitialRequestDto questionInitialRequestDto) {
    final QuestionResponseDto questionResponseDto =
        questionService.getInitialQuestion(questionInitialRequestDto, userId);
    return ResponseEntity.ok(DvApiResponse.of(questionResponseDto));
  }

  @PostMapping("/next-question")
  public ResponseEntity<DvApiResponse<QuestionResponseDto>> getNextQuestion(
      @Validated @RequestBody final QuestionNextRequestDto questionNextRequestDto) {
    final QuestionResponseDto questionResponseDto =
        questionService.getNextQuestion(questionNextRequestDto);
    return ResponseEntity.ok(DvApiResponse.of(questionResponseDto));
  }

  @PostMapping("/completion")
  public ResponseEntity<DvApiResponse<?>> saveQuestionResults(
      @RequestBody @Valid QuestionResultRequestDto questionResultRequestDto) {
    questionService.saveCompletedQuestion(questionResultRequestDto);

    return ResponseEntity.ok(
        DvApiResponse.of(ResponseCode.SUCCESS, "Questions have been successfully saved."));
  }
}
