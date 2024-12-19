package org.richardstallman.dvback.domain.interview.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InterviewRepositoryImpl implements InterviewRepository {

  private final InterviewJpaRepository interviewJpaRepository;
  private final InterviewConverter interviewConverter;

  @Override
  public InterviewDomain save(InterviewDomain interviewDomain) {
    return interviewConverter.fromEntityToDomainWhenCreate(
        interviewJpaRepository.save(
            interviewConverter.fromDomainToEntityWhenCreate(interviewDomain)));
  }

  @Override
  public InterviewDomain reSave(InterviewDomain interviewDomain) {
    return interviewConverter.fromEntityToDomain(
        interviewJpaRepository.save(interviewConverter.fromDomainToEntity(interviewDomain)));
  }

  @Override
  public InterviewDomain findById(Long interviewId) {
    return interviewConverter.fromEntityToDomain(
        interviewJpaRepository.findById(interviewId).orElse(null));
  }

  @Override
  public List<InterviewDomain> findInterviewsByUserId(Long userId) {
    return interviewJpaRepository.findByUserUserIdOrderByInterviewIdDesc(userId).stream()
        .map(interviewConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<InterviewDomain> findInterviewsByUserIdWithEvaluation(Long userId) {
    return interviewJpaRepository
        .findByUserUserIdWithEvaluationsOrderByInterviewIdDesc(userId)
        .stream()
        .map(interviewConverter::fromEntityToDomain)
        .toList();
  }
}
