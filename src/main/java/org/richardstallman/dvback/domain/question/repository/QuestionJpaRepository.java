package org.richardstallman.dvback.domain.question.repository;

import java.util.List;
import org.richardstallman.dvback.domain.question.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {

  List<QuestionEntity> findByInterviewInterviewIdOrderByQuestionIdAsc(Long interviewId);
}
