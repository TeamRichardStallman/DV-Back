package org.richardstallman.dvback.domain.answer.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;

@Builder
@Getter
public class AnswerDomain {

  private final Long answerId;
  private QuestionDomain questionDomain;
  private String answerText;
  private String s3AudioUrl;
  private String s3VideoUrl;
}
