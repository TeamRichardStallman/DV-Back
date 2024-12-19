package org.richardstallman.dvback.domain.answer.service;

import org.richardstallman.dvback.domain.answer.domain.request.AnswerEvaluationRequestDto;

public interface AnswerService {

  void saveAnswerEvaluations(AnswerEvaluationRequestDto answerEvaluationRequestDto);
}
