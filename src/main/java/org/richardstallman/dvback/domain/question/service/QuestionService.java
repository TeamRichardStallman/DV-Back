package org.richardstallman.dvback.domain.question.service;

import jakarta.validation.Valid;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionNextRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionRequestListDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionResultRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;

public interface QuestionService {

  void getQuestionList(@Valid QuestionRequestListDto questionRequestListDto, Long userId);

  QuestionResponseDto getInitialQuestion(
      QuestionInitialRequestDto questionInitialRequestDto, Long userId);

  QuestionResponseDto getNextQuestion(QuestionNextRequestDto questionNextRequestDto);

  void saveCompletedQuestion(QuestionResultRequestDto questionResultRequestDto);
}
