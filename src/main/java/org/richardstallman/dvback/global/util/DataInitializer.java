package org.richardstallman.dvback.global.util;

import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.repository.JobRepository;
import org.richardstallman.dvback.global.config.jobs.JobsConfig;
import org.richardstallman.dvback.global.config.jobs.JobsConfig.Jobs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

  private final JobRepository jobRepository;
  private final JobsConfig jobsConfig;

  public DataInitializer(JobRepository jobRepository, JobsConfig jobsConfig) {
    this.jobRepository = jobRepository;
    this.jobsConfig = jobsConfig;
  }

  @Override
  public void run(String... args) throws Exception {
    if (jobRepository.count() == 0) {
      for (Jobs jobs : jobsConfig.getJobs()) {
        jobRepository.save(
            JobDomain.builder()
                .jobName(jobs.getJobName())
                .jobNameKorean(jobs.getJobNameKorean())
                .jobDescription(jobs.getJobDescription())
                .build());
      }
    }
  }
}
