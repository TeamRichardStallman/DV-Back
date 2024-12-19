package org.richardstallman.dvback.domain.question.domain;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;

@Builder
@Getter
public class QuestionDomain {

  private final Long questionId;
  private InterviewDomain interviewDomain;
  private String questionText;
  private String s3AudioUrl;
  private String s3VideoUrl;
  private String questionExcerpt;
  private String questionIntent;
  private List<String> keyTerms;
  private String questionType;
}
