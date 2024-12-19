package org.richardstallman.dvback.domain.evaluation.domain.external.request.personal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationVoiceScoreDto;

public record EvaluationExternalPersonalAnswerScoreDto(
    @NotNull @JsonProperty("text_scores") EvaluationExternalPersonalAnswerTextScoreDto textScores,
    @JsonProperty("voice_scores") AnswerEvaluationVoiceScoreDto voiceScores) {}
