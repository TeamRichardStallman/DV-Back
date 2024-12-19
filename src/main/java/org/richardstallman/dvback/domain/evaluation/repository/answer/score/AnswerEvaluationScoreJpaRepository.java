package org.richardstallman.dvback.domain.evaluation.repository.answer.score;

import java.util.List;
import org.richardstallman.dvback.domain.evaluation.entity.answer.AnswerEvaluationScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerEvaluationScoreJpaRepository
    extends JpaRepository<AnswerEvaluationScoreEntity, Long> {

  List<AnswerEvaluationScoreEntity> findByAnswerEvaluationEntityAnswerEvaluationId(
      Long answerEvaluationId);

  List<AnswerEvaluationScoreEntity> findByAnswerEvaluationEntityQuestionQuestionId(Long questionId);
}
