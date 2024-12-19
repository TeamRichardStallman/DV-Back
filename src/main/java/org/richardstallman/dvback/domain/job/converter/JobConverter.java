package org.richardstallman.dvback.domain.job.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.entity.JobEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobConverter {

  public JobEntity toEntity(JobDomain jobDomain) {
    return new JobEntity(
        jobDomain.getJobId(),
        jobDomain.getJobName(),
        jobDomain.getJobNameKorean(),
        jobDomain.getJobDescription());
  }

  public JobDomain toDomain(JobEntity jobEntity) {
    return JobDomain.builder()
        .jobId(jobEntity.getJobId())
        .jobName(jobEntity.getJobName())
        .jobNameKorean(jobEntity.getJobNameKorean())
        .jobDescription(jobEntity.getJobDescription())
        .build();
  }
}
