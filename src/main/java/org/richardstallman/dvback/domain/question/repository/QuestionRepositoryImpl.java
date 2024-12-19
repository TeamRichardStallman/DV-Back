package org.richardstallman.dvback.domain.question.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

  private final QuestionJpaRepository questionJpaRepository;
  private final QuestionConverter questionConverter;

  @Override
  public QuestionDomain save(QuestionDomain questionDomain) {
    return questionConverter.fromEntityToDomain(
        questionJpaRepository.save(questionConverter.fromDomainToEntity(questionDomain)));
  }

  @Override
  public Optional<QuestionDomain> findById(Long questionId) {
    return questionJpaRepository.findById(questionId).map(questionConverter::fromEntityToDomain);
  }

  @Override
  public List<QuestionDomain> findQuestionsByInterviewId(Long interviewId) {
    return questionJpaRepository
        .findByInterviewInterviewIdOrderByQuestionIdAsc(interviewId)
        .stream()
        .map(questionConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<QuestionDomain> saveAll(List<QuestionDomain> questionDomains) {
    return questionJpaRepository
        .saveAll(questionDomains.stream().map(questionConverter::fromDomainToEntity).toList())
        .stream()
        .map(questionConverter::fromEntityToDomain)
        .toList();
  }
}
