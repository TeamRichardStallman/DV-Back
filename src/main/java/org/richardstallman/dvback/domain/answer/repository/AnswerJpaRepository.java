package org.richardstallman.dvback.domain.answer.repository;

import org.richardstallman.dvback.domain.answer.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerJpaRepository extends JpaRepository<AnswerEntity, Long> {

  AnswerEntity findByQuestionQuestionId(Long questionId);
}
