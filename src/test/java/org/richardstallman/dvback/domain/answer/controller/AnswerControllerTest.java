package org.richardstallman.dvback.domain.answer.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerEvaluationRequestDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationCriteriaDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationFeedbackDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationScoreDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationTextScoreDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationVoiceScoreDto;
import org.richardstallman.dvback.domain.answer.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnswerControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean private AnswerService answerService;

  @Test
  @DisplayName("파이썬 요청 - 답변 평가 완료 - 성공")
  void save_answer_evaluation() throws Exception {
    // given
    Long userId = 1L;
    Long interviewId = 2L;
    Long questionId = 3L;
    String interviewMethod = "voice";
    AnswerEvaluationDto answerEvaluationDto =
        new AnswerEvaluationDto(
            "answer text",
            "s3 audio url",
            "s3 video url",
            new AnswerEvaluationScoreDto(
                new AnswerEvaluationTextScoreDto(
                    new AnswerEvaluationCriteriaDto(1, "1"),
                    new AnswerEvaluationCriteriaDto(1, "1"),
                    new AnswerEvaluationCriteriaDto(1, "1"),
                    new AnswerEvaluationCriteriaDto(1, "1"),
                    new AnswerEvaluationCriteriaDto(1, "1"),
                    null,
                    null,
                    null,
                    null,
                    null),
                new AnswerEvaluationVoiceScoreDto(
                    new AnswerEvaluationCriteriaDto(1, "1"),
                    new AnswerEvaluationCriteriaDto(1, "1"),
                    new AnswerEvaluationCriteriaDto(1, "1"))),
            new AnswerEvaluationFeedbackDto("strengths", "improvement", "suggestion"));
    AnswerEvaluationRequestDto answerEvaluationRequestDto =
        new AnswerEvaluationRequestDto(
            userId, interviewId, questionId, interviewMethod, answerEvaluationDto);
    ResultActions resultActions =
        mockMvc.perform(
            post("/answer/evaluations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerEvaluationRequestDto)));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(
            jsonPath("message").value("Answer and evaluations have been successfully saved."));

    // restdocs
    resultActions.andDo(
        document(
            "파이썬 요청 - 면접 평가 완료 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Evaluation API")
                    .summary("평가 API")
                    .requestFields(
                        fieldWithPath("user_id").description(""),
                        fieldWithPath("interview_id").description(""),
                        fieldWithPath("question_id").description(""),
                        fieldWithPath("interview_method"),
                        fieldWithPath("answer.answer_text"),
                        fieldWithPath("answer.s3_audio_url"),
                        fieldWithPath("answer.s3_video_url"),
                        fieldWithPath("answer.scores.text_scores.appropriate_response.score"),
                        fieldWithPath("answer.scores.text_scores.appropriate_response.rationale"),
                        fieldWithPath("answer.scores.text_scores.logical_flow.score"),
                        fieldWithPath("answer.scores.text_scores.logical_flow.rationale"),
                        fieldWithPath("answer.scores.text_scores.key_terms.score"),
                        fieldWithPath("answer.scores.text_scores.key_terms.rationale"),
                        fieldWithPath("answer.scores.text_scores.consistency.score"),
                        fieldWithPath("answer.scores.text_scores.consistency.rationale"),
                        fieldWithPath("answer.scores.text_scores.grammatical_errors.score"),
                        fieldWithPath("answer.scores.text_scores.grammatical_errors.rationale"),
                        fieldWithPath("answer.scores.text_scores.teamwork"),
                        fieldWithPath("answer.scores.text_scores.communication"),
                        fieldWithPath("answer.scores.text_scores.problem_solving"),
                        fieldWithPath("answer.scores.text_scores.accountability"),
                        fieldWithPath("answer.scores.text_scores.growth_mindset"),
                        fieldWithPath("answer.scores.voice_scores.wpm.score"),
                        fieldWithPath("answer.scores.voice_scores.wpm.rationale"),
                        fieldWithPath("answer.scores.voice_scores.stutter.score"),
                        fieldWithPath("answer.scores.voice_scores.stutter.rationale"),
                        fieldWithPath("answer.scores.voice_scores.pronunciation.score"),
                        fieldWithPath("answer.scores.voice_scores.pronunciation.rationale"),
                        fieldWithPath("answer.feedback.strengths"),
                        fieldWithPath("answer.feedback.improvement"),
                        fieldWithPath("answer.feedback.suggestion"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("메시지"))
                    .build())));
  }
}
