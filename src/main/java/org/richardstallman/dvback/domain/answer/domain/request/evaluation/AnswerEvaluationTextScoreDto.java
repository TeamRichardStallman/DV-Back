package org.richardstallman.dvback.domain.answer.domain.request.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;

public record AnswerEvaluationTextScoreDto(
    @JsonProperty("appropriate_response") AnswerEvaluationCriteriaDto appropriateResponse,
    @JsonProperty("logical_flow") AnswerEvaluationCriteriaDto logicalFlow,
    @JsonProperty("key_terms") AnswerEvaluationCriteriaDto keyTerms,
    @JsonProperty("consistency") AnswerEvaluationCriteriaDto consistency,
    @JsonProperty("grammatical_errors") AnswerEvaluationCriteriaDto grammaticalErrors,
    @JsonProperty("teamwork") AnswerEvaluationCriteriaDto teamwork,
    @JsonProperty("communication") AnswerEvaluationCriteriaDto communication,
    @JsonProperty("problem_solving") AnswerEvaluationCriteriaDto problemSolving,
    @JsonProperty("accountability") AnswerEvaluationCriteriaDto accountability,
    @JsonProperty("growth_mindset") AnswerEvaluationCriteriaDto growthMindset) {

  public Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> toTechnicalMap() {
    return Map.of(
        AnswerEvaluationScore.APPROPRIATE_RESPONSE, appropriateResponse,
        AnswerEvaluationScore.LOGICAL_FLOW, logicalFlow,
        AnswerEvaluationScore.KEY_TERMS, keyTerms,
        AnswerEvaluationScore.CONSISTENCY, consistency,
        AnswerEvaluationScore.GRAMMATICAL_ERRORS, grammaticalErrors);
  }

  public Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> toPersonalMap() {
    return Map.of(
        AnswerEvaluationScore.TEAMWORK, teamwork,
        AnswerEvaluationScore.COMMUNICATION, communication,
        AnswerEvaluationScore.PROBLEM_SOLVING, problemSolving,
        AnswerEvaluationScore.ACCOUNTABILITY, accountability,
        AnswerEvaluationScore.GROWTH_MINDSET, growthMindset);
  }
}
