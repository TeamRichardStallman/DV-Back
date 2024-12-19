package org.richardstallman.dvback.domain.evaluation.domain.external.request.technical;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationVoiceScoreDto;

public record EvaluationExternalTechnicalAnswerScoreDto(
    @NotNull @JsonProperty("text_scores") EvaluationExternalTechnicalAnswerTextScoreDto textScores,
    @JsonProperty("voice_scores") AnswerEvaluationVoiceScoreDto voiceScores) {}
