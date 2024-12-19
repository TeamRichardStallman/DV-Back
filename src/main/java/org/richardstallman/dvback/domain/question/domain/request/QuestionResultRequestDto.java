package org.richardstallman.dvback.domain.question.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuestionResultRequestDto(
    @NotNull @JsonProperty("user_id") Long userId,
    @NotNull @JsonProperty("interview_id") Long interviewId,
    @NotNull @JsonProperty("questions") List<QuestionResultDto> questions) {}
