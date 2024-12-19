package org.richardstallman.dvback.domain.job.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

  private final JobJpaRepository jobJpaRepository;
  private final JobConverter jobConverter;

  @Override
  public JobDomain save(JobDomain jobDomain) {
    return jobConverter.toDomain(jobJpaRepository.save(jobConverter.toEntity(jobDomain)));
  }

  @Override
  public Optional<JobDomain> findById(Long jobId) {
    return jobJpaRepository.findById(jobId).map(jobConverter::toDomain);
  }

  @Override
  public List<JobDomain> findAll() {
    return jobJpaRepository.findAll().stream()
        .map(jobConverter::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public long count() {
    return jobJpaRepository.count();
  }

  @Override
  public JobDomain findByJobNameKorean(String jobNameKorean) {
    return jobConverter.toDomain(jobJpaRepository.findByJobNameKorean(jobNameKorean));
  }
}
