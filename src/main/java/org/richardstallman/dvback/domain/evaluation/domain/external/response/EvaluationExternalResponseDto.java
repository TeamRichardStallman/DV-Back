package org.richardstallman.dvback.domain.evaluation.domain.external.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.external.AnswerEvaluationExternalDomain;
import org.richardstallman.dvback.domain.evaluation.domain.external.OverallEvaluationExternalDomain;

public record EvaluationExternalResponseDto(
    @NotNull @JsonProperty("answer_evaluations")
        List<AnswerEvaluationExternalDomain> answerEvaluations,
    @NotNull @JsonProperty("overall_evaluation")
        OverallEvaluationExternalDomain overallEvaluation) {}
