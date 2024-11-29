package org.richardstallman.dvback.domain.post.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationType;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.common.constant.CommonConstants.PostType;
import org.richardstallman.dvback.domain.evaluation.domain.answer.response.AnswerEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.AnswerEvaluationScoreResponseDto;
import org.richardstallman.dvback.domain.evaluation.domain.response.EvaluationCriteriaResponseDto;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;
import org.richardstallman.dvback.domain.post.domain.response.PostUserListResponseDto;
import org.richardstallman.dvback.domain.post.service.PostService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private PostService postService;

  @Test
  @WithMockUser
  @DisplayName("게시글 작성 테스트(일반 POST) - 성공")
  void create_post_general_post() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    PostCreateRequestDto requestDto =
        new PostCreateRequestDto(
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            1L,
            2L,
            PostType.POST);

    PostCreateResponseDto responseDto =
        new PostCreateResponseDto(
            100L,
            "BACK_END",
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            null,
            null,
            CommonConstants.PostType.POST,
            LocalDateTime.now());

    when(postService.createPost(any(PostCreateRequestDto.class), eq(userId)))
        .thenReturn(responseDto);

    String content = objectMapper.writeValueAsString(requestDto);
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.postId").value(100L))
        .andExpect(jsonPath("data.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.jobNameKorean").value("백엔드"))
        .andExpect(jsonPath("data.content").value("This is a test post content."))
        .andExpect(jsonPath("data.s3ImageUrl").value("https://s3.amazonaws.com/test-image.jpg"))
        .andExpect(jsonPath("data.postType").value("POST"));

    // restdocs
    resultActions.andDo(
        document(
            "게시글 작성 테스트(일반 POST) - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Post API")
                    .summary("게시글 API")
                    .requestFields(
                        fieldWithPath("jobKoreanName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("s3ImageUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 이미지 URL"),
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("overallEvaluationId")
                            .type(JsonFieldType.NUMBER)
                            .description("종합 평가 식별자"),
                        fieldWithPath("postType")
                            .type(JsonFieldType.STRING)
                            .description("게시글 유형 (EVALUATION, INTERVIEW, POST)"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.postId")
                            .type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.content")
                            .type(JsonFieldType.STRING)
                            .description("게시글 내용"),
                        fieldWithPath("data.s3ImageUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 이미지 URL"),
                        fieldWithPath("data.postType")
                            .type(JsonFieldType.STRING)
                            .description("게시글 유형 (EVALUATION, INTERVIEW, POST)"),
                        fieldWithPath("data.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("작성 일시"),
                        fieldWithPath("data.interview")
                            .type(JsonFieldType.NULL)
                            .description("일반 게시글 작성에서는 면접 정보 없음"),
                        fieldWithPath("data.evaluation")
                            .type(JsonFieldType.NULL)
                            .description("일반 게시글 작성에서는 평가 정보 없음"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("게시글 작성 테스트(면접 공유 INTERVIEW) - 성공")
  void create_post_interview() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    PostCreateRequestDto requestDto =
        new PostCreateRequestDto(
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            1L,
            2L,
            PostType.INTERVIEW);

    InterviewResponseDto interviewResponse =
        new InterviewResponseDto(
            1L,
            "Mock Interview Title",
            InterviewStatus.COMPLETED,
            InterviewType.TECHNICAL,
            InterviewMethod.VIDEO,
            InterviewMode.REAL,
            10,
            JobDomain.builder()
                .jobId(1L)
                .jobName("BACK_END")
                .jobNameKorean("백엔드")
                .jobDescription("백엔드 직무입니다.")
                .build(),
            List.of(
                new FileResponseDto(
                    1L,
                    FileType.COVER_LETTER,
                    "cover_letter.pdf",
                    "https://s3.amazonaws.com/resume.pdf")));

    PostCreateResponseDto responseDto =
        new PostCreateResponseDto(
            100L,
            "BACK_END",
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            interviewResponse,
            null,
            PostType.INTERVIEW,
            LocalDateTime.now());

    when(postService.createPost(any(PostCreateRequestDto.class), eq(userId)))
        .thenReturn(responseDto);

    String content = objectMapper.writeValueAsString(requestDto);
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.postId").value(100L))
        .andExpect(jsonPath("data.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.jobNameKorean").value("백엔드"))
        .andExpect(jsonPath("data.content").value("This is a test post content."))
        .andExpect(jsonPath("data.s3ImageUrl").value("https://s3.amazonaws.com/test-image.jpg"))
        .andExpect(jsonPath("data.postType").value("INTERVIEW"))
        .andExpect(jsonPath("data.interview.interviewTitle").value("Mock Interview Title"));

    // restdocs
    resultActions.andDo(
        document(
            "게시글 작성 테스트(면접 공유 INTERVIEW) - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Post API")
                    .summary("게시글 API")
                    .requestFields(
                        fieldWithPath("jobKoreanName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("s3ImageUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 이미지 URL"),
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("overallEvaluationId")
                            .type(JsonFieldType.NUMBER)
                            .description("종합 평가 식별자"),
                        fieldWithPath("postType")
                            .type(JsonFieldType.STRING)
                            .description("게시글 유형 (EVALUATION, INTERVIEW, POST)"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.postId")
                            .type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.content")
                            .type(JsonFieldType.STRING)
                            .description("게시글 내용"),
                        fieldWithPath("data.s3ImageUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 이미지 URL"),
                        fieldWithPath("data.postType")
                            .type(JsonFieldType.STRING)
                            .description("게시글 유형 (EVALUATION, INTERVIEW, POST)"),
                        fieldWithPath("data.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("작성 일시"),
                        fieldWithPath("data.interview.interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("data.interview.interviewTitle")
                            .type(JsonFieldType.STRING)
                            .description("면접 제목"),
                        fieldWithPath("data.interview.interviewStatus")
                            .type(JsonFieldType.STRING)
                            .description("면접 상태"),
                        fieldWithPath("data.interview.interviewType")
                            .type(JsonFieldType.STRING)
                            .description("면접 유형"),
                        fieldWithPath("data.interview.interviewMethod")
                            .type(JsonFieldType.STRING)
                            .description("면접 방식"),
                        fieldWithPath("data.interview.interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("면접 모드"),
                        fieldWithPath("data.interview.questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description("질문 개수"),
                        fieldWithPath("data.interview.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.interview.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.interview.job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.interview.job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.interview.files[0].fileId")
                            .type(JsonFieldType.NUMBER)
                            .description("파일 식별자"),
                        fieldWithPath("data.interview.files[0].type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형 (예: COVER_LETTER)"),
                        fieldWithPath("data.interview.files[0].fileName")
                            .type(JsonFieldType.STRING)
                            .description("파일 이름"),
                        fieldWithPath("data.interview.files[0].s3FileUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 파일 URL"),
                        fieldWithPath("data.evaluation")
                            .type(JsonFieldType.NULL)
                            .description("면접 공유 게시글 작성에서는 평가 정보 없음"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("게시글 작성 테스트(평가 공유 EVALUATION) - 성공")
  void create_post_evaluation() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    PostCreateRequestDto requestDto =
        new PostCreateRequestDto(
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            1L,
            2L,
            PostType.EVALUATION);

    List<AnswerEvaluationScoreResponseDto> answerEvaluationScores =
        List.of(
            new AnswerEvaluationScoreResponseDto(
                1L, "Clarity", 8, "Clear explanation", AnswerEvaluationType.TEXT.name()),
            new AnswerEvaluationScoreResponseDto(
                2L, "Conciseness", 7, "Could be more concise", AnswerEvaluationType.TEXT.name()));

    List<AnswerEvaluationResponseDto> answerEvaluations =
        List.of(
            new AnswerEvaluationResponseDto(
                1L,
                "What is your greatest strength?",
                "I am highly adaptable.",
                "Good emphasis on adaptability.",
                "Could elaborate more.",
                "Provide an example of adaptability.",
                answerEvaluationScores));

    List<EvaluationCriteriaResponseDto> evaluationCriteria =
        List.of(
            new EvaluationCriteriaResponseDto(1L, "Communication Skills", "Clear and concise.", 9),
            new EvaluationCriteriaResponseDto(2L, "Problem-Solving", "Good examples provided.", 8));

    InterviewResponseDto interviewResponse =
        new InterviewResponseDto(
            1L,
            "Mock Interview Title",
            InterviewStatus.COMPLETED,
            InterviewType.TECHNICAL,
            InterviewMethod.VIDEO,
            InterviewMode.REAL,
            10,
            JobDomain.builder()
                .jobId(1L)
                .jobName("BACK_END")
                .jobNameKorean("백엔드")
                .jobDescription("백엔드 직무입니다.")
                .build(),
            List.of(
                new FileResponseDto(
                    1L,
                    FileType.COVER_LETTER,
                    "cover_letter.pdf",
                    "https://s3.amazonaws.com/resume.pdf")));

    OverallEvaluationResponseDto overallEvaluationResponse =
        new OverallEvaluationResponseDto(interviewResponse, evaluationCriteria, answerEvaluations);

    PostCreateResponseDto responseDto =
        new PostCreateResponseDto(
            100L,
            "BACK_END",
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            null,
            overallEvaluationResponse,
            PostType.EVALUATION,
            LocalDateTime.now());

    when(postService.createPost(any(PostCreateRequestDto.class), eq(userId)))
        .thenReturn(responseDto);

    String content = objectMapper.writeValueAsString(requestDto);
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.postId").value(100L))
        .andExpect(jsonPath("data.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.jobNameKorean").value("백엔드"))
        .andExpect(jsonPath("data.content").value("This is a test post content."))
        .andExpect(jsonPath("data.s3ImageUrl").value("https://s3.amazonaws.com/test-image.jpg"))
        .andExpect(jsonPath("data.postType").value("EVALUATION"))
        .andExpect(
            jsonPath("data.evaluation.evaluationCriteria[0].evaluationCriteria")
                .value("Communication Skills"))
        .andExpect(
            jsonPath("data.evaluation.answerEvaluations[0].questionText")
                .value("What is your greatest strength?"))
        .andExpect(
            jsonPath("data.evaluation.answerEvaluations[0].answerEvaluationScores[0].score")
                .value(8));

    // restdocs
    resultActions.andDo(
        document(
            "게시글 작성 테스트(평가 공유 EVALUATION) - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Post API")
                    .summary("게시글 API")
                    .requestFields(
                        fieldWithPath("jobKoreanName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("s3ImageUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 이미지 URL"),
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("overallEvaluationId")
                            .type(JsonFieldType.NUMBER)
                            .description("종합 평가 식별자"),
                        fieldWithPath("postType")
                            .type(JsonFieldType.STRING)
                            .description("게시글 유형 (EVALUATION, INTERVIEW, POST)"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.postId")
                            .type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.content")
                            .type(JsonFieldType.STRING)
                            .description("게시글 내용"),
                        fieldWithPath("data.s3ImageUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 이미지 URL"),
                        fieldWithPath("data.postType")
                            .type(JsonFieldType.STRING)
                            .description("게시글 유형 (EVALUATION, INTERVIEW, POST)"),
                        fieldWithPath("data.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("작성 일시"),
                        fieldWithPath("data.interview")
                            .type(JsonFieldType.NULL)
                            .description("평가 공유 게시글 작성에서는 인터뷰 정보 없음"),
                        fieldWithPath("data.evaluation.interview.interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("data.evaluation.interview.interviewTitle")
                            .type(JsonFieldType.STRING)
                            .description("면접 제목"),
                        fieldWithPath("data.evaluation.interview.interviewStatus")
                            .type(JsonFieldType.STRING)
                            .description("면접 상태"),
                        fieldWithPath("data.evaluation.interview.interviewType")
                            .type(JsonFieldType.STRING)
                            .description("면접 유형"),
                        fieldWithPath("data.evaluation.interview.interviewMethod")
                            .type(JsonFieldType.STRING)
                            .description("면접 방식"),
                        fieldWithPath("data.evaluation.interview.interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("면접 모드"),
                        fieldWithPath("data.evaluation.interview.questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description("질문 개수"),
                        fieldWithPath("data.evaluation.interview.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.evaluation.interview.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.evaluation.interview.job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.evaluation.interview.job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.evaluation.interview.files[0].fileId")
                            .type(JsonFieldType.NUMBER)
                            .description("파일 식별자"),
                        fieldWithPath("data.evaluation.interview.files[0].type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형 (예: COVER_LETTER)"),
                        fieldWithPath("data.evaluation.interview.files[0].fileName")
                            .type(JsonFieldType.STRING)
                            .description("파일 이름"),
                        fieldWithPath("data.evaluation.interview.files[0].s3FileUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 파일 URL"),
                        fieldWithPath("data.evaluation.evaluationCriteria[0].evaluationCriteriaId")
                            .type(JsonFieldType.NUMBER)
                            .description("평가 기준 식별자"),
                        fieldWithPath("data.evaluation.evaluationCriteria[0].evaluationCriteria")
                            .type(JsonFieldType.STRING)
                            .description("평가 기준"),
                        fieldWithPath("data.evaluation.evaluationCriteria[0].feedbackText")
                            .type(JsonFieldType.STRING)
                            .description("평가 피드백 텍스트"),
                        fieldWithPath("data.evaluation.evaluationCriteria[0].score")
                            .type(JsonFieldType.NUMBER)
                            .description("평가 점수"),
                        fieldWithPath("data.evaluation.answerEvaluations[0].answerEvaluationId")
                            .type(JsonFieldType.NUMBER)
                            .description("답변 평가 식별자"),
                        fieldWithPath("data.evaluation.answerEvaluations[0].questionText")
                            .type(JsonFieldType.STRING)
                            .description("질문 텍스트"),
                        fieldWithPath("data.evaluation.answerEvaluations[0].answerText")
                            .type(JsonFieldType.STRING)
                            .description("답변 텍스트"),
                        fieldWithPath("data.evaluation.answerEvaluations[0].answerFeedbackStrength")
                            .type(JsonFieldType.STRING)
                            .description("답변 강점 피드백"),
                        fieldWithPath(
                                "data.evaluation.answerEvaluations[0].answerFeedbackImprovement")
                            .type(JsonFieldType.STRING)
                            .description("답변 개선 피드백"),
                        fieldWithPath(
                                "data.evaluation.answerEvaluations[0].answerFeedbackSuggestion")
                            .type(JsonFieldType.STRING)
                            .description("답변 제안 피드백"),
                        fieldWithPath(
                                "data.evaluation.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreId")
                            .type(JsonFieldType.NUMBER)
                            .description("답변 평가 점수 식별자"),
                        fieldWithPath(
                                "data.evaluation.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreName")
                            .type(JsonFieldType.STRING)
                            .description("답변 평가 점수 이름"),
                        fieldWithPath(
                                "data.evaluation.answerEvaluations[0].answerEvaluationScores[0].score")
                            .type(JsonFieldType.NUMBER)
                            .description("답변 평가 점수"),
                        fieldWithPath(
                                "data.evaluation.answerEvaluations[0].answerEvaluationScores[0].rationale")
                            .type(JsonFieldType.STRING)
                            .description("답변 평가 점수 근거"),
                        fieldWithPath(
                                "data.evaluation.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationType")
                            .type(JsonFieldType.STRING)
                            .description("답변 평가 유형(TEXT, VOICE, VIDEO"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저가 작성한 게시글 목록 최신 작성순 조회 - 성공")
  void get_user_post_list() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    PostCreateRequestDto requestDto =
        new PostCreateRequestDto(
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            1L,
            2L,
            PostType.EVALUATION);

    List<AnswerEvaluationScoreResponseDto> answerEvaluationScores =
        List.of(
            new AnswerEvaluationScoreResponseDto(
                1L, "Clarity", 8, "Clear explanation", AnswerEvaluationType.TEXT.name()),
            new AnswerEvaluationScoreResponseDto(
                2L, "Conciseness", 7, "Could be more concise", AnswerEvaluationType.TEXT.name()));

    List<AnswerEvaluationResponseDto> answerEvaluations =
        List.of(
            new AnswerEvaluationResponseDto(
                1L,
                "What is your greatest strength?",
                "I am highly adaptable.",
                "Good emphasis on adaptability.",
                "Could elaborate more.",
                "Provide an example of adaptability.",
                answerEvaluationScores));

    List<EvaluationCriteriaResponseDto> evaluationCriteria =
        List.of(
            new EvaluationCriteriaResponseDto(1L, "Communication Skills", "Clear and concise.", 9),
            new EvaluationCriteriaResponseDto(2L, "Problem-Solving", "Good examples provided.", 8));

    InterviewResponseDto interviewResponse =
        new InterviewResponseDto(
            1L,
            "Mock Interview Title",
            InterviewStatus.COMPLETED,
            InterviewType.TECHNICAL,
            InterviewMethod.VIDEO,
            InterviewMode.REAL,
            10,
            JobDomain.builder()
                .jobId(1L)
                .jobName("BACK_END")
                .jobNameKorean("백엔드")
                .jobDescription("백엔드 직무입니다.")
                .build(),
            List.of(
                new FileResponseDto(
                    1L,
                    FileType.COVER_LETTER,
                    "cover_letter.pdf",
                    "https://s3.amazonaws.com/resume.pdf")));
    OverallEvaluationResponseDto overallEvaluationResponse =
        new OverallEvaluationResponseDto(interviewResponse, evaluationCriteria, answerEvaluations);

    PostCreateResponseDto responseDto1 =
        new PostCreateResponseDto(
            100L,
            "BACK_END",
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            null,
            overallEvaluationResponse,
            PostType.EVALUATION,
            LocalDateTime.now());

    PostCreateResponseDto responseDto2 =
        new PostCreateResponseDto(
            108L,
            "BACK_END",
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            interviewResponse,
            null,
            PostType.INTERVIEW,
            LocalDateTime.now());

    PostCreateResponseDto responseDto3 =
        new PostCreateResponseDto(
            132L,
            "BACK_END",
            "백엔드",
            "This is a test post content.",
            "https://s3.amazonaws.com/test-image.jpg",
            null,
            null,
            PostType.POST,
            LocalDateTime.now());

    List<PostCreateResponseDto> posts = new ArrayList<>();
    posts.add(responseDto1);
    posts.add(responseDto2);
    posts.add(responseDto3);
    PostUserListResponseDto postUserListResponseDto = new PostUserListResponseDto(posts);

    when(postService.getPostsByUserId(eq(userId))).thenReturn(posts).thenReturn(posts);

    String content = objectMapper.writeValueAsString(requestDto);
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/post/user")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.posts[0].postId").value(100));

    // restdocs
    resultActions.andDo(
        document(
            "유저가 작성한 게시글 목록 조회 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Post API")
                    .summary("게시글 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.posts[].postId")
                            .type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.posts[].jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.posts[].jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.posts[].content")
                            .type(JsonFieldType.STRING)
                            .description("게시글 내용"),
                        fieldWithPath("data.posts[].s3ImageUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 이미지 URL"),
                        fieldWithPath("data.posts[].postType")
                            .type(JsonFieldType.STRING)
                            .description("게시글 유형 (EVALUATION, INTERVIEW, POST)"),
                        fieldWithPath("data.posts[].generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("작성 일시"),
                        fieldWithPath("data.posts[].interview")
                            .optional()
                            .description("면접 공유 시(면접 정보)"),
                        fieldWithPath("data.posts[].interview.interviewId").description("면접 식별자"),
                        fieldWithPath("data.posts[].interview.interviewTitle").description("면접 제목"),
                        fieldWithPath("data.posts[].interview.interviewStatus")
                            .description("면접 상태"),
                        fieldWithPath("data.posts[].interview.interviewType").description("면접 유형"),
                        fieldWithPath("data.posts[].interview.interviewMethod")
                            .description("면접 방식"),
                        fieldWithPath("data.posts[].interview.interviewMode").description("면접 모드"),
                        fieldWithPath("data.posts[].interview.questionCount").description("질문 개수"),
                        fieldWithPath("data.posts[].interview.job.jobId").description("직무 식별자"),
                        fieldWithPath("data.posts[].interview.job.jobName").description("직무 이름"),
                        fieldWithPath("data.posts[].interview.job.jobNameKorean")
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.posts[].interview.job.jobDescription")
                            .description("직무 설명"),
                        fieldWithPath("data.posts[].interview.files[0].fileId")
                            .description("파일 식별자"),
                        fieldWithPath("data.posts[].interview.files[0].type")
                            .description("파일 유형 (예: COVER_LETTER)"),
                        fieldWithPath("data.posts[].interview.files[0].fileName")
                            .description("파일 이름"),
                        fieldWithPath("data.posts[].interview.files[0].s3FileUrl")
                            .description("S3 파일 URL"),
                        fieldWithPath("data.posts[].evaluation")
                            .optional()
                            .description("면접 공유 게시글 작성에서는 평가 정보 없음"),
                        fieldWithPath("data.posts[].evaluation.interview.interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("data.posts[].evaluation.interview.interviewTitle")
                            .type(JsonFieldType.STRING)
                            .description("면접 제목"),
                        fieldWithPath("data.posts[].evaluation.interview.interviewStatus")
                            .type(JsonFieldType.STRING)
                            .description("면접 상태"),
                        fieldWithPath("data.posts[].evaluation.interview.interviewType")
                            .type(JsonFieldType.STRING)
                            .description("면접 유형"),
                        fieldWithPath("data.posts[].evaluation.interview.interviewMethod")
                            .type(JsonFieldType.STRING)
                            .description("면접 방식"),
                        fieldWithPath("data.posts[].evaluation.interview.interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("면접 모드"),
                        fieldWithPath("data.posts[].evaluation.interview.questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description("질문 개수"),
                        fieldWithPath("data.posts[].evaluation.interview.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.posts[].evaluation.interview.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.posts[].evaluation.interview.job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.posts[].evaluation.interview.job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.posts[].evaluation.interview.files[0].fileId")
                            .type(JsonFieldType.NUMBER)
                            .description("파일 식별자"),
                        fieldWithPath("data.posts[].evaluation.interview.files[0].type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형 (예: COVER_LETTER)"),
                        fieldWithPath("data.posts[].evaluation.interview.files[0].fileName")
                            .type(JsonFieldType.STRING)
                            .description("파일 이름"),
                        fieldWithPath("data.posts[].evaluation.interview.files[0].s3FileUrl")
                            .type(JsonFieldType.STRING)
                            .description("S3 파일 URL"),
                        fieldWithPath(
                                "data.posts[].evaluation.evaluationCriteria[0].evaluationCriteriaId")
                            .type(JsonFieldType.NUMBER)
                            .description("평가 기준 식별자"),
                        fieldWithPath(
                                "data.posts[].evaluation.evaluationCriteria[0].evaluationCriteria")
                            .type(JsonFieldType.STRING)
                            .description("평가 기준"),
                        fieldWithPath("data.posts[].evaluation.evaluationCriteria[0].feedbackText")
                            .type(JsonFieldType.STRING)
                            .description("평가 피드백 텍스트"),
                        fieldWithPath("data.posts[].evaluation.evaluationCriteria[0].score")
                            .type(JsonFieldType.NUMBER)
                            .description("평가 점수"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerEvaluationId")
                            .type(JsonFieldType.NUMBER)
                            .description("답변 평가 식별자"),
                        fieldWithPath("data.posts[].evaluation.answerEvaluations[0].questionText")
                            .type(JsonFieldType.STRING)
                            .description("질문 텍스트"),
                        fieldWithPath("data.posts[].evaluation.answerEvaluations[0].answerText")
                            .type(JsonFieldType.STRING)
                            .description("답변 텍스트"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerFeedbackStrength")
                            .type(JsonFieldType.STRING)
                            .description("답변 강점 피드백"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerFeedbackImprovement")
                            .type(JsonFieldType.STRING)
                            .description("답변 개선 피드백"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerFeedbackSuggestion")
                            .type(JsonFieldType.STRING)
                            .description("답변 제안 피드백"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreId")
                            .type(JsonFieldType.NUMBER)
                            .description("답변 평가 점수 식별자"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationScoreName")
                            .type(JsonFieldType.STRING)
                            .description("답변 평가 점수 이름"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerEvaluationScores[0].score")
                            .type(JsonFieldType.NUMBER)
                            .description("답변 평가 점수"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerEvaluationScores[0].rationale")
                            .type(JsonFieldType.STRING)
                            .description("답변 평가 점수 근거"),
                        fieldWithPath(
                                "data.posts[].evaluation.answerEvaluations[0].answerEvaluationScores[0].answerEvaluationType")
                            .type(JsonFieldType.STRING)
                            .description("답변 평가 유형(TEXT, VOICE, VIDEO"))
                    .build())));
  }
}
