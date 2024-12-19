package org.richardstallman.dvback.domain.evaluation.repository.answer.score;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationScoreDomain;

public interface AnswerEvaluationScoreRepository {

  AnswerEvaluationScoreDomain save(AnswerEvaluationScoreDomain answerEvaluationScoreDomain);

  List<AnswerEvaluationScoreDomain> saveAll(
      List<AnswerEvaluationScoreDomain> answerEvaluationScoreDomains);

  List<AnswerEvaluationScoreDomain> findByAnswerEvaluationId(Long answerEvaluationId);

  List<AnswerEvaluationScoreDomain> findByQuestionId(Long questionId);
}
