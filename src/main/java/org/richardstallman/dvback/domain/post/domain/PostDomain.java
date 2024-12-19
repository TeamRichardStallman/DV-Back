package org.richardstallman.dvback.domain.post.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.PostType;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Builder
@Getter
public class PostDomain {

  private final Long postId;
  private final UserDomain authorDomain;
  private JobDomain jobDomain;
  private String content;
  private String s3ImageUrl;
  private InterviewDomain interviewDomain;
  private OverallEvaluationDomain overallEvaluationDomain;
  private PostType postType;
  private LocalDateTime generatedAt;
}
