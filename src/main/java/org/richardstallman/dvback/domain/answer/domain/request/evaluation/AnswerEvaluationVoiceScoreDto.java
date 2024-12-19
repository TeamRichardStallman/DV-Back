package org.richardstallman.dvback.domain.answer.domain.request.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;

public record AnswerEvaluationVoiceScoreDto(
    @NotNull @JsonProperty("wpm") AnswerEvaluationCriteriaDto wpm,
    @NotNull @JsonProperty("stutter") AnswerEvaluationCriteriaDto stutter,
    @NotNull @JsonProperty("pronunciation") AnswerEvaluationCriteriaDto pronunciation) {

  public Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> toMap() {
    return Map.of(
        AnswerEvaluationScore.WPM, wpm,
        AnswerEvaluationScore.STUTTER, stutter,
        AnswerEvaluationScore.PRONUNCIATION, pronunciation);
  }
}
