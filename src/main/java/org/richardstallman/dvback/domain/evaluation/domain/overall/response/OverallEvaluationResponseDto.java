package org.richardstallman.dvback.domain.evaluation.domain.overall.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;

public record OverallEvaluationResponseDto(
    @NotNull InterviewResponseDto interview,
    @NotNull List<EvaluationCriteriaResponseDto> evaluationCriteria,
    @NotNull List<AnswerEvaluationResponseDto> answerEvaluations) {}
