package org.richardstallman.dvback.domain.question.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerPreviousRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewQuestionResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.question.domain.request.QuestionDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionNextRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionRequestListDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionResultDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionResultRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;
import org.richardstallman.dvback.domain.question.service.QuestionService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuestionControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private QuestionService questionService;

  @Test
  @WithMockUser
  @DisplayName("질문 목록 생성 요청(모의 면접 성공) 테스트")
  void request_question_list_general() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    QuestionRequestListDto questionRequestListDto =
        new QuestionRequestListDto(
            1L,
            "면접 제목",
            InterviewStatus.FILE_UPLOADED,
            InterviewType.TECHNICAL,
            InterviewMethod.CHAT,
            InterviewMode.GENERAL,
            3,
            new ArrayList<>(),
            1L);
    String content = objectMapper.writeValueAsString(questionRequestListDto);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    ResultActions resultActions =
        mockMvc.perform(
            post("/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions.andExpect(status().isAccepted());

    // restdocs
    resultActions.andDo(
        document(
            "질문 목록 생성 요청(모의 면접 성공) 테스트",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Question API")
                    .summary("질문 API")
                    .requestFields(
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("interviewTitle")
                            .type(JsonFieldType.STRING)
                            .description("면접 제목"),
                        fieldWithPath("interviewStatus")
                            .type(JsonFieldType.STRING)
                            .description("면접 상태"),
                        fieldWithPath("interviewType")
                            .type(JsonFieldType.STRING)
                            .description("면접 유형"),
                        fieldWithPath("interviewMethod")
                            .type(JsonFieldType.STRING)
                            .description("면접 방식"),
                        fieldWithPath("interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("면접 모드"),
                        fieldWithPath("questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description(
                                "질문 개수: 모의(3개 고정. 다르게 보내줘도 3으로 저장됨.), 실전(5 / 10 / 15 중 유저가 선택)"),
                        fieldWithPath("files")
                            .type(JsonFieldType.ARRAY)
                            .description("모의 면접이므로 파일 정보 없어야 함."),
                        fieldWithPath("jobId").type(JsonFieldType.NUMBER).description("직무 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("질문 목록 생성 요청(실전 면접 성공) 테스트")
  void request_question_list_real() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    FileType fileType = FileType.COVER_LETTER;
    String file = "cover_letter_s3_url/file_name";
    List<FileRequestDto> files = new ArrayList<>();
    files.add(new FileRequestDto(fileType, file));
    QuestionRequestListDto questionRequestListDto =
        new QuestionRequestListDto(
            1L,
            "면접 제목",
            InterviewStatus.FILE_UPLOADED,
            InterviewType.TECHNICAL,
            InterviewMethod.CHAT,
            InterviewMode.GENERAL,
            3,
            files,
            1L);
    String content = objectMapper.writeValueAsString(questionRequestListDto);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    ResultActions resultActions =
        mockMvc.perform(
            post("/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions.andExpect(status().isAccepted());

    // restdocs
    resultActions.andDo(
        document(
            "질문 목록 생성 요청(실전 면접 성공) 테스트",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Question API")
                    .summary("질문 API")
                    .requestFields(
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("interviewTitle")
                            .type(JsonFieldType.STRING)
                            .description("면접 제목"),
                        fieldWithPath("interviewStatus")
                            .type(JsonFieldType.STRING)
                            .description("면접 상태"),
                        fieldWithPath("interviewType")
                            .type(JsonFieldType.STRING)
                            .description("면접 유형"),
                        fieldWithPath("interviewMethod")
                            .type(JsonFieldType.STRING)
                            .description("면접 방식"),
                        fieldWithPath("interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("면접 모드"),
                        fieldWithPath("questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description(
                                "질문 개수: 모의(3개 고정. 다르게 보내줘도 3으로 저장됨.), 실전(5 / 10 / 15 중 유저가 선택)"),
                        fieldWithPath("files[0].filePath")
                            .type(JsonFieldType.STRING)
                            .description("파일 s3 url"),
                        fieldWithPath("files[0].type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형: COVER_LETTER(자소서), RESUME(이력서), PORTFOLIO(포트폴리오)"),
                        fieldWithPath("jobId").type(JsonFieldType.NUMBER).description("직무 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("질문 요청 - 최초 요청(모의 면접 성공) 테스트")
  void get_question_by_request_python_server_general() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    QuestionInitialRequestDto questionInitialRequestDto = new QuestionInitialRequestDto(1L);
    String content = objectMapper.writeValueAsString(questionInitialRequestDto);

    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(questionService.getInitialQuestion(any(), eq(1L)))
        .thenReturn(
            new QuestionResponseDto(
                new InterviewQuestionResponseDto(
                    1L,
                    "면접 제목",
                    InterviewStatus.FILE_UPLOADED,
                    InterviewType.TECHNICAL,
                    InterviewMethod.CHAT,
                    InterviewMode.GENERAL,
                    3,
                    JobDomain.builder()
                        .jobId(1L)
                        .jobName("BACK_END")
                        .jobNameKorean("백엔드")
                        .jobDescription("백엔드 직무입니다.")
                        .build()),
                1L,
                "스타크래프트를 처음으로 접한 경험을 통해 어떻게 최고를 목표로 삼고 성취했는지 이야기해보세요.",
                "",
                2L,
                "스타크래프트를 처음으로 접한 경험을 통해 어떻게 최고를 목표로 삼고 성취했는지 이야기해보세요2.",
                "",
                true));
    ResultActions resultActions =
        mockMvc.perform(
            post("/question/initial-question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.interview.interviewId").value(1))
        .andExpect(jsonPath("data.interview.interviewTitle").value("면접 제목"))
        .andExpect(jsonPath("data.interview.interviewStatus").value("FILE_UPLOADED"))
        .andExpect(jsonPath("data.interview.interviewType").value("TECHNICAL"))
        .andExpect(jsonPath("data.interview.interviewMethod").value("CHAT"))
        .andExpect(jsonPath("data.interview.interviewMode").value("GENERAL"))
        .andExpect(jsonPath("data.interview.job.jobId").value(1L))
        .andExpect(jsonPath("data.interview.job.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.interview.job.jobNameKorean").value("백엔드"))
        .andExpect(jsonPath("data.interview.job.jobDescription").value("백엔드 직무입니다."))
        .andExpect(
            jsonPath("data.currentQuestionText")
                .value("스타크래프트를 처음으로 접한 경험을 통해 어떻게 최고를 목표로 삼고 성취했는지 이야기해보세요."))
        .andExpect(jsonPath("data.nextQuestionId").value(2L))
        .andExpect(jsonPath("data.hasNext").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "질문 요청 - 최초 요청(모의 면접 성공)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Question API")
                    .summary("질문 API")
                    .requestFields(
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
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
                            .description("면접 질문 개수"),
                        fieldWithPath("data.interview.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.interview.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.interview.job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 한글 이름"),
                        fieldWithPath("data.interview.job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.currentQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("현재 질문 식별자"),
                        fieldWithPath("data.currentQuestionText")
                            .type(JsonFieldType.STRING)
                            .description("현재 질문 내용"),
                        fieldWithPath("data.currentQuestionS3AudioUrl")
                            .type(JsonFieldType.STRING)
                            .description("현재 질문 음성 파일 주소"),
                        fieldWithPath("data.nextQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("다음 질문 식별자"),
                        fieldWithPath("data.nextQuestionText")
                            .type(JsonFieldType.STRING)
                            .description("다음 질문 내용"),
                        fieldWithPath("data.nextQuestionS3AudioUrl")
                            .type(JsonFieldType.STRING)
                            .description("다음 질문 음성 파일 주소"),
                        fieldWithPath("data.hasNext")
                            .type(JsonFieldType.BOOLEAN)
                            .description("다음 질문 존재 여부"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("질문 요청 - 최초 요청(실전 면접 성공) 테스트")
  void get_question_by_request_python_server_real() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(1L);
    FileType fileType = FileType.COVER_LETTER;
    String file = "cover_letter_s3_url/file_name";
    List<FileRequestDto> files = new ArrayList<>();
    files.add(new FileRequestDto(fileType, file));
    QuestionInitialRequestDto questionInitialRequestDto = new QuestionInitialRequestDto(1L);
    String content = objectMapper.writeValueAsString(questionInitialRequestDto);

    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(questionService.getInitialQuestion(any(), eq(userId)))
        .thenReturn(
            new QuestionResponseDto(
                new InterviewQuestionResponseDto(
                    1L,
                    "면접 제목",
                    InterviewStatus.FILE_UPLOADED,
                    InterviewType.TECHNICAL,
                    InterviewMethod.CHAT,
                    InterviewMode.REAL,
                    5,
                    JobDomain.builder()
                        .jobId(1L)
                        .jobName("BACK_END")
                        .jobNameKorean("백엔드")
                        .jobDescription("백엔드 직무입니다.")
                        .build()),
                1L,
                "스타크래프트를 처음으로 접한 경험을 통해 어떻게 최고를 목표로 삼고 성취했는지 이야기해보세요.",
                "",
                2L,
                "다음 질문입니다.",
                "",
                true));
    ResultActions resultActions =
        mockMvc.perform(
            post("/question/initial-question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.interview.interviewId").value(1))
        .andExpect(jsonPath("data.interview.interviewTitle").value("면접 제목"))
        .andExpect(jsonPath("data.interview.interviewStatus").value("FILE_UPLOADED"))
        .andExpect(jsonPath("data.interview.interviewType").value("TECHNICAL"))
        .andExpect(jsonPath("data.interview.interviewMethod").value("CHAT"))
        .andExpect(jsonPath("data.interview.interviewMode").value("REAL"))
        .andExpect(jsonPath("data.interview.job.jobId").value(1L))
        .andExpect(jsonPath("data.interview.job.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.interview.job.jobNameKorean").value("백엔드"))
        .andExpect(jsonPath("data.interview.job.jobDescription").value("백엔드 직무입니다."))
        .andExpect(
            jsonPath("data.currentQuestionText")
                .value("스타크래프트를 처음으로 접한 경험을 통해 어떻게 최고를 목표로 삼고 성취했는지 이야기해보세요."))
        .andExpect(jsonPath("data.nextQuestionId").value(2L))
        .andExpect(jsonPath("data.hasNext").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "질문 요청 - 최초 요청(실전 면접 성공)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Question API")
                    .summary("질문 API")
                    .requestFields(
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
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
                            .description("면접 질문 개수"),
                        fieldWithPath("data.interview.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.interview.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.interview.job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 한글 이름"),
                        fieldWithPath("data.interview.job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.currentQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("현재 질문 식별자"),
                        fieldWithPath("data.currentQuestionText")
                            .type(JsonFieldType.STRING)
                            .description("현재 질문 내용"),
                        fieldWithPath("data.currentQuestionS3AudioUrl")
                            .type(JsonFieldType.STRING)
                            .description("현재 질문 음성 파일 주소"),
                        fieldWithPath("data.nextQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("다음 질문 식별자"),
                        fieldWithPath("data.nextQuestionText")
                            .type(JsonFieldType.STRING)
                            .description("다음 질문 내용"),
                        fieldWithPath("data.nextQuestionS3AudioUrl")
                            .type(JsonFieldType.STRING)
                            .description("다음 질문 음성 파일 주소"),
                        fieldWithPath("data.hasNext")
                            .type(JsonFieldType.BOOLEAN)
                            .description("다음 질문 존재 여부"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("다음 질문 요청(텍스트 성공) 테스트")
  void get_next_question_test() throws Exception {
    // given
    AnswerPreviousRequestDto answerPreviousRequestDto =
        new AnswerPreviousRequestDto("답변입니다.", "", "");
    QuestionNextRequestDto questionNextRequestDto =
        new QuestionNextRequestDto(1L, 1L, 2L, answerPreviousRequestDto);
    String content = objectMapper.writeValueAsString(questionNextRequestDto);

    when(questionService.getNextQuestion(any()))
        .thenReturn(
            new QuestionResponseDto(
                new InterviewQuestionResponseDto(
                    1L,
                    "면접 제목",
                    InterviewStatus.FILE_UPLOADED,
                    InterviewType.TECHNICAL,
                    InterviewMethod.CHAT,
                    InterviewMode.GENERAL,
                    3,
                    JobDomain.builder()
                        .jobId(1L)
                        .jobName("BACK_END")
                        .jobNameKorean("백엔드")
                        .jobDescription("백엔드 직무입니다.")
                        .build()),
                1L,
                "리액트와 스프링 간의 연동 경험을 설명해 주세요.",
                "",
                2L,
                "협업 과정에서 가장 힘들었던 일이 있다면 무엇이었나요?",
                "",
                true));

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/question/next-question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.interview.interviewId").value(1))
        .andExpect(jsonPath("data.interview.interviewTitle").value("면접 제목"))
        .andExpect(jsonPath("data.interview.interviewStatus").value("FILE_UPLOADED"))
        .andExpect(jsonPath("data.interview.interviewType").value("TECHNICAL"))
        .andExpect(jsonPath("data.interview.interviewMethod").value("CHAT"))
        .andExpect(jsonPath("data.interview.interviewMode").value("GENERAL"))
        .andExpect(jsonPath("data.interview.job.jobId").value(1L))
        .andExpect(jsonPath("data.interview.job.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.interview.job.jobNameKorean").value("백엔드"))
        .andExpect(jsonPath("data.interview.job.jobDescription").value("백엔드 직무입니다."))
        .andExpect(jsonPath("data.currentQuestionText").value("리액트와 스프링 간의 연동 경험을 설명해 주세요."))
        .andExpect(jsonPath("data.nextQuestionId").value(2L))
        .andExpect(jsonPath("data.hasNext").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "다음 질문 요청(텍스트 성공)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Question API")
                    .summary("질문 API")
                    .requestFields(
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("answer.s3AudioUrl")
                            .type(JsonFieldType.STRING)
                            .description("답변 오디오 s3 저장 url"),
                        fieldWithPath("answer.s3VideoUrl")
                            .type(JsonFieldType.STRING)
                            .description("답변 비디오 s3 저장 url"),
                        fieldWithPath("answer.answerText")
                            .type(JsonFieldType.STRING)
                            .description("이전 질문에 대한 답변"),
                        fieldWithPath("answerQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("전달되는 답변에 대한 질문 식별자"),
                        fieldWithPath("nextQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("반환해줘야 하는 다음 질문 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
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
                            .description(
                                "질문 개수: 모의(3개 고정. 다르게 보내줘도 3으로 저장됨.), 실전(5 / 10 / 15 중 유저가 선택)"),
                        fieldWithPath("data.interview.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.interview.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.interview.job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 한글 이름"),
                        fieldWithPath("data.interview.job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.currentQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("현재 질문 식별자"),
                        fieldWithPath("data.currentQuestionText")
                            .type(JsonFieldType.STRING)
                            .description("현재 질문 내용"),
                        fieldWithPath("data.currentQuestionS3AudioUrl")
                            .type(JsonFieldType.STRING)
                            .description("현재 질문 음성 파일 주소"),
                        fieldWithPath("data.nextQuestionId")
                            .type(JsonFieldType.NUMBER)
                            .description("다음 질문 식별자"),
                        fieldWithPath("data.nextQuestionText")
                            .type(JsonFieldType.STRING)
                            .description("다음 질문 내용"),
                        fieldWithPath("data.nextQuestionS3AudioUrl")
                            .type(JsonFieldType.STRING)
                            .description("다음 질문 음성 파일 주소"),
                        fieldWithPath("data.hasNext")
                            .type(JsonFieldType.BOOLEAN)
                            .description("다음 질문 존재 여부"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("파이썬 요청 - 질문 목록 생성 완료 - 성공")
  void save_question_list() throws Exception {
    // given
    Long userId = 1L;
    Long interviewId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    List<QuestionResultDto> list = new ArrayList<>();
    QuestionResultDto questionResultDto1 =
        new QuestionResultDto(
            new QuestionDto("question_text", "s3_audio_url", null),
            "question_excerpt",
            "question_intent",
            new ArrayList<>());
    QuestionResultDto questionResultDto2 =
        new QuestionResultDto(
            new QuestionDto("question_text", "s3_audio_url", null),
            "question_excerpt",
            "question_intent",
            new ArrayList<>());
    QuestionResultDto questionResultDto3 =
        new QuestionResultDto(
            new QuestionDto("question_text", "s3_audio_url", null),
            "question_excerpt",
            "question_intent",
            new ArrayList<>());
    list.add(questionResultDto1);
    list.add(questionResultDto2);
    list.add(questionResultDto3);
    QuestionResultRequestDto questionResultRequestDto =
        new QuestionResultRequestDto(userId, interviewId, list);

    String content = objectMapper.writeValueAsString(questionResultRequestDto);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    ResultActions resultActions =
        mockMvc.perform(
            post("/question/completion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions.andExpect(status().isOk());

    // restdocs
    resultActions.andDo(
        document(
            "파이썬 요청 - 질문 목록 생성 완료 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Question API")
                    .summary("질문 API")
                    .requestFields(
                        fieldWithPath("user_id").type(JsonFieldType.NUMBER).description("유저 식별자"),
                        fieldWithPath("interview_id")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("questions[].question.question_text")
                            .type(JsonFieldType.STRING)
                            .description("질문 내용"),
                        fieldWithPath("questions[].question.s3_audio_url")
                            .type(JsonFieldType.STRING)
                            .description("질문 url"),
                        fieldWithPath("questions[].question.s3_video_url")
                            .type(JsonFieldType.NULL)
                            .description("질문 url"),
                        fieldWithPath("questions[].question_excerpt")
                            .type(JsonFieldType.STRING)
                            .description("질문 출처"),
                        fieldWithPath("questions[].question_intent")
                            .type(JsonFieldType.STRING)
                            .description("질문 의도"),
                        fieldWithPath("questions[].key_terms")
                            .type(JsonFieldType.ARRAY)
                            .description("핵심 단어"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터"))
                    .build())));
  }
}
