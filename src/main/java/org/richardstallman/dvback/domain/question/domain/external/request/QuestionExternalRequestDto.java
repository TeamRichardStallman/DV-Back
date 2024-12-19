package org.richardstallman.dvback.domain.question.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;

public record QuestionExternalRequestDto(
    @JsonProperty("user_id") @NotNull Long userId,
    @JsonProperty("interview_mode") @NotNull String interviewMode,
    @JsonProperty("interview_type") @NotNull String interviewType,
    @JsonProperty("interview_method") @NotNull String interviewMethod,
    @JsonProperty("question_count") @NotNull Integer questionCount,
    @JsonProperty("job_role") @NotNull String jobName,
    @JsonProperty("file_paths") String[] filePaths) {

  public QuestionExternalRequestDto(
      Long userId,
      InterviewMode interviewMode,
      InterviewType interviewType,
      InterviewMethod interviewMethod,
      Integer questionCount,
      String jobName,
      String[] filePaths) {
    this(
        userId,
        interviewMode.getPythonFormat(),
        interviewType.getPythonFormat(),
        interviewMethod.getPythonFormat(),
        questionCount,
        convertJobNameToPythonFormat(jobName),
        filePaths);
  }

  private static String convertJobNameToPythonFormat(String jobName) {
    return switch (jobName) {
      case "BACK_END" -> "backend";
      case "FRONT_END" -> "frontend";
      case "INFRA" -> "infra";
      case "AI" -> "ai";
      default -> jobName.toLowerCase();
    };
  }
}
