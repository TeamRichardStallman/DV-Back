package org.richardstallman.dvback.domain.evaluation.domain.answer.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.domain.evaluation.domain.response.AnswerEvaluationScoreResponseDto;

public record AnswerEvaluationResponseDto(
    @NotNull Long answerEvaluationId,
    @NotNull String questionText,
    @NotNull String answerText,
    String answerS3PresignedUrl,
    @NotNull String answerFeedbackStrength,
    @NotNull String answerFeedbackImprovement,
    @NotNull String answerFeedbackSuggestion,
    @NotNull List<AnswerEvaluationScoreResponseDto> answerEvaluationScores) {}
