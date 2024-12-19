package org.richardstallman.dvback.domain.evaluation.domain.external.request.technical;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationCriteriaDto;

public record EvaluationExternalTechnicalAnswerTextScoreDto(
    @JsonProperty("appropriate_response") AnswerEvaluationCriteriaDto appropriateResponse,
    @JsonProperty("logical_flow") AnswerEvaluationCriteriaDto logicalFlow,
    @JsonProperty("key_terms") AnswerEvaluationCriteriaDto keyTerms,
    @JsonProperty("consistency") AnswerEvaluationCriteriaDto consistency,
    @JsonProperty("grammatical_errors") AnswerEvaluationCriteriaDto grammaticalErrors) {}
