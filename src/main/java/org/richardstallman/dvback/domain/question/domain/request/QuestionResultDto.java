package org.richardstallman.dvback.domain.question.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuestionResultDto(
    @NotNull @JsonProperty("question") QuestionDto question,
    @JsonProperty("question_excerpt") String questionExcerpt,
    @NotNull @JsonProperty("question_intent") String questionIntent,
    @NotNull @JsonProperty("key_terms") List<String> keyTerms) {}
