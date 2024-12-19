package org.richardstallman.dvback.domain.evaluation.repository.answer;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;

public interface AnswerEvaluationRepository {

  AnswerEvaluationDomain save(AnswerEvaluationDomain answerEvaluationDomain);

  List<AnswerEvaluationDomain> saveAll(List<AnswerEvaluationDomain> answerEvaluationDomains);

  List<AnswerEvaluationDomain> findByInterviewId(Long interviewId);

  AnswerEvaluationDomain findByQuestionId(Long questionId);

  long countByInterviewId(Long interviewId);
}
