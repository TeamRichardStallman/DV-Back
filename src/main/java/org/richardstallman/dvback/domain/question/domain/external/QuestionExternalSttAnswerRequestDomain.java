package org.richardstallman.dvback.domain.question.domain.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuestionExternalSttAnswerRequestDomain(
    @JsonProperty("answer_text") String answerText,
    @JsonProperty("s3_audio_url") String s3AudioUrl,
    @JsonProperty("s3_video_url") String s3VideoUrl) {}
