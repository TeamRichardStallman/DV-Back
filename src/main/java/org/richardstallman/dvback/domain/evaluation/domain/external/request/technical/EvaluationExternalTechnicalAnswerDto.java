package org.richardstallman.dvback.domain.evaluation.domain.external.request.technical;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationFeedbackDto;

public record EvaluationExternalTechnicalAnswerDto(
    @JsonProperty("answer_text") String answerText,
    @JsonProperty("s3_audio_url") String s3AudioUrl,
    @JsonProperty("s3_video_url") String s3VideoUrl,
    @NotNull @JsonProperty("scores") EvaluationExternalTechnicalAnswerScoreDto scores,
    @NotNull @JsonProperty("feedback") AnswerEvaluationFeedbackDto feedback) {}
