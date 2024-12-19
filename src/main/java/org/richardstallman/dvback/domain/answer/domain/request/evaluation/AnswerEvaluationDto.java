package org.richardstallman.dvback.domain.answer.domain.request.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record AnswerEvaluationDto(
    @JsonProperty("answer_text") String answerText,
    @JsonProperty("s3_audio_url") String s3AudioUrl,
    @JsonProperty("s3_video_url") String s3VideoUrl,
    @NotNull @JsonProperty("scores") AnswerEvaluationScoreDto scores,
    @NotNull @JsonProperty("feedback") AnswerEvaluationFeedbackDto feedback) {}
