package org.richardstallman.dvback.domain.question.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionExternalContentDomain {

  @NotNull @JsonProperty("question_text")
  private String questionText;

  @JsonProperty("s3_audio_url")
  private String s3AudioUrl;

  @JsonProperty("s3_video_url")
  private String s3VideoUrl;
}
