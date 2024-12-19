package org.richardstallman.dvback.domain.evaluation.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore.APPROPRIATE_RESPONSE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationType;
import org.richardstallman.dvback.common.constant.CommonConstants.EvaluationCriteria;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultCriteriaDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultRequestDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultTextDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationResultVoiceDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.AnswerEvaluationScoreResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.evaluation.service.EvaluationService;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EvaluationControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired JwtUtil jwtUtil;

  @Autowired ObjectMapper objectMapper;

  @MockBean private EvaluationService evaluationService;

  @Test
  @DisplayName("마이페이지 - 면접 평가 조회 - 성공")
  void getMyPageInterviewEvaluation() throws Exception {
    // given
    Long userId = 1L;
    Long interviewId = 1L;
    Long jobId = 1L;
    String jobName = "BACK_END";
    String jobNameKorean = "백엔드";
    String jobDescription = "백엔드 직무입니다.";

    String interviewTitle = "면접 제목";
    InterviewStatus interviewStatus = InterviewStatus.INITIAL;
    InterviewType interviewType = InterviewType.TECHNICAL;
    InterviewMethod interviewMethod = InterviewMethod.CHAT;
    InterviewMode interviewMode = InterviewMode.GENERAL;
    JobDomain job =
        JobDomain.builder()
            .jobId(jobId)
            .jobName(jobName)
            .jobNameKorean(jobNameKorean)
            .jobDescription(jobDescription)
            .build();

    Long evaluationCriteriaId = 1L;
    String evaluationCriteria = EvaluationCriteria.GROWTH_POTENTIAL.name();
    String feedbackText = "피드백입니다.";
    int score = 1;

    InterviewResponseDto interviewCreateResponseDto =
        new InterviewResponseDto(
            interviewId,
            interviewTitle,
            interviewStatus,
            interviewType,
            interviewMethod,
            interviewMode,
            5,
            job,
            new ArrayList<>());
    List<EvaluationCriteriaResponseDto> evaluationCriteriaResponseDtos = new ArrayList<>();
    evaluationCriteriaResponseDtos.add(
        new EvaluationCriteriaResponseDto(
            evaluationCriteriaId, evaluationCriteria, feedbackText, score));

    Long answerEvaluationId = 1L;
    String questionText = "질문 텍스트 입니다.";
    String answerText = "답변 텍스트 입니다.";
    String answerFeedbackStrength = "answerFeedbackStrength";
    String answerFeedbackImprovement = "answerFeedbackImprovement";
    String answerFeedbackSuggestion = "answerFeedbackSuggestion";
    List<AnswerEvaluationScoreResponseDto> answerEvaluationScoreResponseDtos = new ArrayList<>();
    Long answerEvaluationScoreId = 1L;
    String answerEvaluationScoreName = APPROPRIATE_RESPONSE.name();
    int answerEvaluationScore = 4;
    String rationale = "Excellent, thorough response.";
    answerEvaluationScoreResponseDtos.add(
        new AnswerEvaluationScoreResponseDto(
            answerEvaluationScoreId,
            answerEvaluationScoreName,
            answerEvaluationScore,
            rationale,
            AnswerEvaluationType.TEXT.name()));

    List<AnswerEvaluationResponseDto> answerEvaluationResponseDtos = new ArrayList<>();
    answerEvaluationResponseDtos.add(
        new AnswerEvaluationResponseDto(
            answerEvaluationId,
            questionText,
            answerText,
            "https://bucket.com",
            answerFeedbackStrength,
            answerFeedbackImprovement,
            answerFeedbackSuggestion,
            answerEvaluationScoreResponseDtos));

    OverallEvaluationResponseDto overallEvaluationResponseDto =
        new OverallEvaluationResponseDto(
            interviewCreateResponseDto,
            evaluationCriteriaResponseDtos,
            answerEvaluationResponseDtos);

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(evaluationService.getOverallEvaluationByInterviewId(eq(interviewId)))
        .thenReturn(overallEvaluationResponseDto);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/evaluation/{interviewId}", interviewId)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.interview.interviewId").value(interviewId))
        .andExpect(jsonPath("data.interview.interviewTitle").value(interviewTitle))
        .andExpect(jsonPath("data.interview.interviewStatus").value(interviewStatus.name()))
        .andExpect(jsonPath("data.interview.interviewType").value(interviewType.name()))
        .andExpect(jsonPath("data.interview.interviewMethod").value(interviewMethod.name()))
        .andExpect(jsonPath("data.interview.interviewMode").value(interviewMode.name()))
        .andExpect(jsonPath("data.interview.job.jobId").value(1L))
        .andExpect(jsonPath("data.interview.job.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.interview.job.jobNameKorean").value(jobNameKorean))
        .andExpect(jsonPath("data.interview.job.jobDescription").value("백엔드 직무입니다."))
        .andExpect(
            jsonPath("data.evaluationCriteria[0].evaluationCriteriaId").value(evaluationCriteriaId))
        .andExpect(
            jsonPath("data.evaluationCriteria[0].evaluationCriteria").value(evaluationCriteria))
        .andExpect(jsonPath("data.evaluationCriteria[0].feedbackText").value(feedbackText))
        .andExpect(jsonPath("data.evaluationCriteria[0].score").value(score))
        .andExpect(
            jsonPath("data.answerEvaluations[0].answerEvaluationId").value(answerEvaluationId))
        .andExpect(jsonPath("data.answerEvaluations[0].questionText").value(questionText))
        .andExpect(jsonPath("data.answerEvaluations[0].answerText").value(answerText))
        .andExpect(
            jsonPath("data.answerEvaluations[0].answerFeedbackStrength")
                .value(answerFeedbackStrength))
        .andExpect(
            jsonPath("data.answerEvaluations[0].answerFeedbackImprovement")
                .value(answerFeedbackImprovement))
        .andExpect(
            jsonPath("data.answerEvaluations[0].answerFeedbackSuggestion")
                .value(answerFeedbackSuggestion))
        .andExpect(
            jsonPath("data.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreId")
                .value(answerEvaluationScoreId))
        .andExpect(
            jsonPath(
                    "data.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreName")
                .value(answerEvaluationScoreName))
        .andExpect(
            jsonPath("data.answerEvaluations[0].answerEvaluationScores[0].score")
                .value(answerEvaluationScore))
        .andExpect(
            jsonPath("data.answerEvaluations[0].answerEvaluationScores[0].rationale")
                .value(rationale));
    // restdocs
    resultActions.andDo(
        document(
            "마이페이지 - 면접 평가 조회 - 성공",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Evaluation API")
                    .summary("평가 API")
                    .pathParameters(parameterWithName("interviewId").description("면접 식별자"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.interview.interviewId").description("면접 식별자"),
                        fieldWithPath("data.interview.interviewTitle").description("면접 제목"),
                        fieldWithPath("data.interview.interviewStatus").description("면접 상태"),
                        fieldWithPath("data.interview.interviewType").description("면접 유형"),
                        fieldWithPath("data.interview.interviewMethod").description("면접 방식"),
                        fieldWithPath("data.interview.interviewMode").description("면접 모드"),
                        fieldWithPath("data.interview.questionCount").description("면접 질문 개수"),
                        fieldWithPath("data.interview.job.jobId").description("직무 식별자"),
                        fieldWithPath("data.interview.job.jobName").description("직무 이름"),
                        fieldWithPath("data.interview.job.jobNameKorean").description("직무 이름(한국어)"),
                        fieldWithPath("data.interview.job.jobDescription").description("직무 설명"),
                        fieldWithPath("data.interview.files").description("파일 목록"),
                        fieldWithPath("data.evaluationCriteria[0].evaluationCriteriaId")
                            .description("평가 기준 식별자"),
                        fieldWithPath("data.evaluationCriteria[0].evaluationCriteria")
                            .description("평가 기준"),
                        fieldWithPath("data.evaluationCriteria[0].feedbackText")
                            .description("피드백 텍스트"),
                        fieldWithPath("data.evaluationCriteria[0].score").description("점수"),
                        fieldWithPath("data.answerEvaluations[0].answerEvaluationId")
                            .description("답변 평가 식별자"),
                        fieldWithPath("data.answerEvaluations[0].questionText")
                            .description("질문 텍스트"),
                        fieldWithPath("data.answerEvaluations[0].answerText").description("답변 텍스트"),
                        fieldWithPath("data.answerEvaluations[0].answerS3PresignedUrl")
                            .description("답변 텍스트 음성 url"),
                        fieldWithPath("data.answerEvaluations[0].answerFeedbackStrength")
                            .description("답변의 강점 피드백"),
                        fieldWithPath("data.answerEvaluations[0].answerFeedbackImprovement")
                            .description("답변의 개선 사항 피드백"),
                        fieldWithPath("data.answerEvaluations[0].answerFeedbackSuggestion")
                            .description("답변 개선 제안"),
                        fieldWithPath(
                                "data.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreId")
                            .description("답변 평가 점수 식별자"),
                        fieldWithPath(
                                "data.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreName")
                            .description("답변 평가 점수 이름"),
                        fieldWithPath("data.answerEvaluations[0].answerEvaluationScores[0].score")
                            .description("점수"),
                        fieldWithPath(
                                "data.answerEvaluations[0].answerEvaluationScores[0].rationale")
                            .description("평가 근거"),
                        fieldWithPath(
                                "data.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationType")
                            .description("평가 유형(TEXT, VOICE)"))
                    .build())));
  }

  @Test
  @DisplayName("파이썬 요청 - 면접 평가 완료 - 성공")
  void save_evaluation_results() throws Exception {
    // given
    Long userId = 1L;
    Long interviewId = 1L;
    OverallEvaluationResultRequestDto overallEvaluationResultRequestDto =
        new OverallEvaluationResultRequestDto(
            userId,
            interviewId,
            new OverallEvaluationResultDto(
                new OverallEvaluationResultTextDto(
                    new OverallEvaluationResultCriteriaDto(1, "11"),
                    new OverallEvaluationResultCriteriaDto(1, "11"),
                    new OverallEvaluationResultCriteriaDto(1, "11"),
                    new OverallEvaluationResultCriteriaDto(1, "11"),
                    null,
                    null,
                    null,
                    null),
                new OverallEvaluationResultVoiceDto(
                    new OverallEvaluationResultCriteriaDto(1, "11"),
                    new OverallEvaluationResultCriteriaDto(1, "11"),
                    new OverallEvaluationResultCriteriaDto(1, "11"))));

    String result = "Evaluations have been successfully saved.";

    ResultActions resultActions =
        mockMvc.perform(
            post("/evaluation/completion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overallEvaluationResultRequestDto)));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("Evaluations have been successfully saved."));

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
                        fieldWithPath("user_id").description("유저 식별자"),
                        fieldWithPath("interview_id").description("면접 식별자"),
                        fieldWithPath("overall_evaluation.text_overall.job_fit.score")
                            .description("텍스트 직무 적합도 점수"),
                        fieldWithPath("overall_evaluation.text_overall.job_fit.rationale")
                            .description("텍스트 직무 적합도 평가 내용"),
                        fieldWithPath("overall_evaluation.text_overall.growth_potential.score")
                            .description("텍스트 잠재력 점수"),
                        fieldWithPath("overall_evaluation.text_overall.growth_potential.rationale")
                            .description("텍스트 잠재력 평가 내용"),
                        fieldWithPath("overall_evaluation.text_overall.work_attitude.score")
                            .description("텍스트 업무 태도 점수"),
                        fieldWithPath("overall_evaluation.text_overall.work_attitude.rationale")
                            .description("텍스트 업무 태도 평가 내용"),
                        fieldWithPath("overall_evaluation.text_overall.technical_depth.score")
                            .description("텍스트 기술 점수"),
                        fieldWithPath("overall_evaluation.text_overall.technical_depth.rationale")
                            .description("텍스트 기술 평가 내용"),
                        fieldWithPath("overall_evaluation.text_overall.company_fit")
                            .description("텍스트 기업"),
                        fieldWithPath("overall_evaluation.text_overall.adaptability")
                            .description("텍스트 적응력"),
                        fieldWithPath("overall_evaluation.text_overall.interpersonal_skills")
                            .description("텍스트 대인관계"),
                        fieldWithPath("overall_evaluation.text_overall.growth_attitude")
                            .description("텍스트 성장"),
                        fieldWithPath("overall_evaluation.voice_overall.fluency.score")
                            .description("음성 유창함 점수"),
                        fieldWithPath("overall_evaluation.voice_overall.fluency.rationale")
                            .description("음성 유창함 평가 내용"),
                        fieldWithPath("overall_evaluation.voice_overall.clarity.score")
                            .description("음성 명료함 점수"),
                        fieldWithPath("overall_evaluation.voice_overall.clarity.rationale")
                            .description("음성 명료함 평가 내용"),
                        fieldWithPath("overall_evaluation.voice_overall.word_repetition.score")
                            .description("음성 단어 반복 점수"),
                        fieldWithPath("overall_evaluation.voice_overall.word_repetition.rationale")
                            .description("음성 단어 반복 평가 내용"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("메시지"))
                    .build())));
  }
}
