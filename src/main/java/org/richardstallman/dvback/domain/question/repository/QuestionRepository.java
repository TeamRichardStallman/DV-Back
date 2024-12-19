package org.richardstallman.dvback.domain.question.repository;

import java.util.List;
import java.util.Optional;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;

public interface QuestionRepository {

  QuestionDomain save(QuestionDomain questionDomain);

  Optional<QuestionDomain> findById(Long questionId);

  List<QuestionDomain> findQuestionsByInterviewId(Long interviewId);

  List<QuestionDomain> saveAll(List<QuestionDomain> questionDomains);
}
