package org.richardstallman.dvback.domain.question.domain.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.EvaluationExternalQuestionRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalSttAnswerRequestDomain;

public record QuestionExternalSttRequestDto(
    @NotNull @JsonProperty("user_id") Long userId,
    @NotNull @JsonProperty("interview_mode") String interviewMode,
    @NotNull @JsonProperty("interview_type") String interviewType,
    @NotNull @JsonProperty("interview_method") String interviewMethod,
    @NotNull @JsonProperty("job_role") String jobName,
    @NotNull @JsonProperty("file_paths") List<String> filePaths,
    @NotNull @JsonProperty("question") EvaluationExternalQuestionRequestDto question,
    @NotNull @JsonProperty("answer") QuestionExternalSttAnswerRequestDomain answer) {

  public QuestionExternalSttRequestDto(
      Long userId,
      InterviewMode interviewMode,
      InterviewType interviewType,
      InterviewMethod interviewMethod,
      String jobName,
      List<String> filePaths,
      EvaluationExternalQuestionRequestDto question,
      QuestionExternalSttAnswerRequestDomain answer) {
    this(
        userId,
        interviewMode.getPythonFormat(),
        interviewType.getPythonFormat(),
        interviewMethod.getPythonFormat(),
        convertJobNameToPythonFormat(jobName),
        filePaths,
        question,
        answer);
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
