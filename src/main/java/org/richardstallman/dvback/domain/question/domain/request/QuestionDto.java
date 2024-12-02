package org.richardstallman.dvback.domain.question.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record QuestionDto(
    @NotNull @JsonProperty("question_text") String questionText,
    @JsonProperty("s3_audio_url") String s3AudioUrl,
    @JsonProperty("s3_video_url") String s3VideoUrl) {}
