package org.richardstallman.dvback.domain.job.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobDomain {

  private Long jobId;
  private String jobName;
  private String jobNameKorean;
  private String jobDescription;
}
