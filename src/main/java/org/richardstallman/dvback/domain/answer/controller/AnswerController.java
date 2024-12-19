package org.richardstallman.dvback.domain.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants.ResponseCode;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerEvaluationRequestDto;
import org.richardstallman.dvback.domain.answer.service.AnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/answer")
public class AnswerController {

  private final AnswerService answerService;

  @PostMapping("/evaluations")
  public ResponseEntity<DvApiResponse<?>> saveAnswerEvaluation(
      @Valid @RequestBody AnswerEvaluationRequestDto answerEvaluationRequestDto) {
    log.info("/evaluations python requested with : ({})", answerEvaluationRequestDto);
    answerService.saveAnswerEvaluations(answerEvaluationRequestDto);
    return ResponseEntity.ok(
        DvApiResponse.of(
            ResponseCode.SUCCESS, "Answer and evaluations have been successfully saved."));
  }
}
