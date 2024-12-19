package org.richardstallman.dvback.domain.question.domain.external.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalDomain;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionExternalResponseDto {

  @JsonProperty("questions")
  private List<QuestionExternalDomain> questions;
}
