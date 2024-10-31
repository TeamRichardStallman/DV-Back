package org.richardstallman.dvback.domain.evaluation.repository.answer;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;

public interface AnswerEvaluationRepository {

  AnswerEvaluationDomain save(AnswerEvaluationDomain answerEvaluationDomain);

  List<AnswerEvaluationDomain> saveAll(List<AnswerEvaluationDomain> answerEvaluationDomains);
}