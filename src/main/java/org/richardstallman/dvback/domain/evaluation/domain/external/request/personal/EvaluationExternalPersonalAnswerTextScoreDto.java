package org.richardstallman.dvback.domain.evaluation.domain.external.request.personal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationCriteriaDto;

public record EvaluationExternalPersonalAnswerTextScoreDto(
    @JsonProperty("teamwork") AnswerEvaluationCriteriaDto teamwork,
    @JsonProperty("communication") AnswerEvaluationCriteriaDto communication,
    @JsonProperty("problem_solving") AnswerEvaluationCriteriaDto problemSolving,
    @JsonProperty("accountability") AnswerEvaluationCriteriaDto accountability,
    @JsonProperty("growth_mindset") AnswerEvaluationCriteriaDto growthMindset) {}
