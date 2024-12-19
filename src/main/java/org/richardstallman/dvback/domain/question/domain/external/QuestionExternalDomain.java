package org.richardstallman.dvback.domain.question.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionExternalDomain {

  @NotNull @JsonProperty("question_id")
  Long questionId;

  @NotNull @JsonProperty("question")
  QuestionExternalContentDomain question;

  @JsonProperty("question_excerpt")
  String questionExcerpt;

  @NotNull @JsonProperty("question_intent")
  String questionIntent;

  @NotNull @JsonProperty("key_terms")
  List<String> keyTerms;
}
