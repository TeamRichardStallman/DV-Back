package org.richardstallman.dvback.domain.evaluation.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EvaluationExternalQuestionRequestDto(
    @JsonProperty("question_id") @NotNull Long questionId,
    @JsonProperty("question") @NotNull EvaluationExternalQuestionDto question,
    @JsonProperty("question_excerpt") String questionExcerpt,
    @JsonProperty("question_intent") @NotNull String questionIntent,
    @JsonProperty("key_terms") @NotNull List<String> keyTerms) {}
