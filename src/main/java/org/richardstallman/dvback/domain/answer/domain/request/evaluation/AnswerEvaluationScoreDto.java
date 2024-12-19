package org.richardstallman.dvback.domain.answer.domain.request.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AnswerEvaluationScoreDto(
    @NotNull @JsonProperty("text_scores") AnswerEvaluationTextScoreDto textScores,
    @JsonProperty("voice_scores") AnswerEvaluationVoiceScoreDto voiceScores) {}
