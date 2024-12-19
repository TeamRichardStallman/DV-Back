package org.richardstallman.dvback.domain.answer.repository;

import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;

public interface AnswerRepository {

  AnswerDomain save(AnswerDomain answerDomain);

  AnswerDomain update(AnswerDomain answerDomain);

  AnswerDomain findByQuestionId(Long questionId);
}
