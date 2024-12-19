package org.richardstallman.dvback.domain.job.service;

import java.util.List;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

public interface JobService {

  JobDomain createJob(JobDomain jobDomain);

  JobDomain findJobById(Long jobId);

  List<JobDomain> findAllJobs();

  JobDomain findJobByKoreanName(String jobNameKorean);
}
