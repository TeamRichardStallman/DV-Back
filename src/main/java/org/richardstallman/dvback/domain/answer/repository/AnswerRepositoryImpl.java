package org.richardstallman.dvback.domain.answer.repository;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.answer.converter.AnswerConverter;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepository {

  private final AnswerJpaRepository answerJpaRepository;
  private final AnswerConverter answerConverter;

  @Override
  public AnswerDomain save(AnswerDomain answerDomain) {
    return answerConverter.fromEntityToDomain(
        answerJpaRepository.save(answerConverter.fromDomainToEntityWhenCreate(answerDomain)));
  }

  @Override
  public AnswerDomain update(AnswerDomain answerDomain) {
    return answerConverter.fromEntityToDomain(
        answerJpaRepository.save(answerConverter.fromDomainToEntity(answerDomain)));
  }

  @Override
  public AnswerDomain findByQuestionId(Long questionId) {
    return answerConverter.fromEntityToDomain(
        answerJpaRepository.findByQuestionQuestionId(questionId));
  }
}
