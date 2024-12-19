package org.richardstallman.dvback.domain.evaluation.domain.external.request.technical;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionRequestDto;

public record EvaluationExternalTechnicalRequestDto(
    @JsonProperty("user_id") @NotNull Long userId,
    @JsonProperty("interview_mode") @NotNull String interviewMode,
    @JsonProperty("interview_type") @NotNull String interviewType,
    @JsonProperty("interview_method") @NotNull String interviewMethod,
    @JsonProperty("job_role") @NotNull String jobName,
    @JsonProperty("questions") @NotNull List<EvaluationExternalQuestionRequestDto> questions,
    @JsonProperty("answers") @NotNull List<EvaluationExternalTechnicalAnswerRequestDto> answers,
    @JsonProperty("file_paths") List<String> filePaths) {
  public EvaluationExternalTechnicalRequestDto(
      Long userId,
      InterviewMode interviewMode,
      InterviewType interviewType,
      InterviewMethod interviewMethod,
      String jobName,
      List<EvaluationExternalQuestionRequestDto> questions,
      List<EvaluationExternalTechnicalAnswerRequestDto> answers,
      List<String> filePaths) {
    this(
        userId,
        interviewMode.getPythonFormat(),
        interviewType.getPythonFormat(),
        interviewMethod.getPythonFormat(),
        convertJobNameToPythonFormat(jobName),
        questions,
        answers,
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
