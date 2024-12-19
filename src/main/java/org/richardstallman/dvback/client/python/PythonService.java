package org.richardstallman.dvback.client.python;

import org.richardstallman.dvback.domain.evaluation.domain.external.request.personal.EvaluationExternalPersonalRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.external.request.technical.EvaluationExternalTechnicalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalSttRequestDto;

public interface PythonService {

  void requestQuestionList(QuestionExternalRequestDto requestDto, Long interviewId);

  void getTechnicalOverallEvaluations(
      EvaluationExternalTechnicalRequestDto requestDto, Long interviewId);

  void getPersonalOverallEvaluations(
      EvaluationExternalPersonalRequestDto requestDto, Long interviewId);

  void sendAnswer(
      QuestionExternalSttRequestDto questionExternalSttRequestDto, Long interviewId, Long answerId);
}
