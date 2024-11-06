package org.richardstallman.dvback.domain.interview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
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
public class InterviewControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private InterviewService interviewService;

  @Test
  @WithMockUser
  @DisplayName("면접 정보 입력 - 면접 저장 테스트")
  void create_interview_by_save_interview_info() throws Exception {

    // given
    Long userId = 1L;
    List<FileRequestDto> files = new ArrayList<>();
    String timestamp = String.valueOf(System.currentTimeMillis());
    String accessToken = jwtUtil.generateAccessToken(1L);

    files.add(
        new CoverLetterRequestDto(
            FileType.COVER_LETTER, "COVER_LETTER/1/" + timestamp + "/coverLetterName"));

    InterviewCreateRequestDto interviewCreateRequestDto =
        new InterviewCreateRequestDto(
            "면접 제목", InterviewType.TECHNICAL, InterviewMethod.VIDEO, InterviewMode.REAL, 2L, files);

    MockCookie authCookie = new MockCookie("access_token", accessToken);

    String content = objectMapper.writeValueAsString(interviewCreateRequestDto);

    List<FileResponseDto> fileResponseDtos = new ArrayList<>();
    fileResponseDtos.add(
        new FileResponseDto(
            1L,
            FileType.COVER_LETTER,
            "coverLetterName",
            "COVER_LETTER/1/" + timestamp + "/coverLetterName"));

    when(interviewService.createInterview(any(), eq(userId)))
        .thenReturn(
            new InterviewCreateResponseDto(
                1L,
                "면접 제목",
                InterviewStatus.INITIAL,
                InterviewType.TECHNICAL,
                InterviewMethod.VIDEO,
                InterviewMode.REAL,
                JobDomain.builder()
                    .jobId(1L)
                    .jobName("BACK_END")
                    .jobNameKorean("백엔드")
                    .jobDescription("백엔드 직무입니다.")
                    .build(),
                fileResponseDtos));
    ResultActions resultActions =
        mockMvc.perform(
            post("/interview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.interviewId").value(1))
        .andExpect(jsonPath("data.interviewStatus").value("INITIAL"))
        .andExpect(jsonPath("data.interviewType").value("TECHNICAL"))
        .andExpect(jsonPath("data.interviewMethod").value("VIDEO"))
        .andExpect(jsonPath("data.interviewMode").value("REAL"))
        .andExpect(jsonPath("data.job.jobId").value(1L))
        .andExpect(jsonPath("data.job.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.job.jobNameKorean").value("백엔드"))
        .andExpect(jsonPath("data.job.jobDescription").value("백엔드 직무입니다."));

    // restdocs
    resultActions.andDo(
        document(
            "면접 정보 입력 - 면접 저장 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("interviewTitle").type(JsonFieldType.STRING).description("면접 제목"),
                fieldWithPath("interviewType")
                    .type(JsonFieldType.STRING)
                    .description("면접 유형: TECHNICAL(기술 면접), PERSONAL(인성 면접)"),
                fieldWithPath("interviewMethod")
                    .type(JsonFieldType.STRING)
                    .description("면접 방식: CHAT(채팅 면접), VOICE(음성 면접), VIDEO(영상 면접)"),
                fieldWithPath("interviewMode")
                    .type(JsonFieldType.STRING)
                    .description("면접 모드: REAL(실전 면접 모드), GENERAL(일반/모의 면접 모드)"),
                fieldWithPath("jobId").type(JsonFieldType.NUMBER).description("직무 식별자"),
                fieldWithPath("files[0].type")
                    .type(JsonFieldType.STRING)
                    .description("파일 유형: COVER_LETTER, RESUME, PORTFOLIO"),
                fieldWithPath("files[0].filePath")
                    .type(JsonFieldType.STRING)
                    .description("파일 경로: {파일 유형}/{유저 식별자}/{timestamp}/{파일명}")),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data.interviewId").type(JsonFieldType.NUMBER).description("면접 식별자"),
                fieldWithPath("data.interviewTitle")
                    .type(JsonFieldType.STRING)
                    .description("면접 제목"),
                fieldWithPath("data.interviewStatus")
                    .type(JsonFieldType.STRING)
                    .description(
                        "면접 상태: INITIAL(최초 정보 입력 상태), FILE_UPLOADED(자소서 및 파일 업로드 완료 상태), WAITING_FOR_QUESTION(질문 생성 요청 후 대기 상태), (READY면접 시작 가능 상태), IN_PROGRESS(면접 진행 중 상태), WAITING_FOR_FEEDBACK(피드백 생성 요청 후 대기 상태), COMPLETED(피드백 완료 상태)"),
                fieldWithPath("data.interviewType")
                    .type(JsonFieldType.STRING)
                    .description("면접 유형: TECHNICAL(기술 면접), PERSONAL(인성 면접)"),
                fieldWithPath("data.interviewMethod")
                    .type(JsonFieldType.STRING)
                    .description("면접 방식: CHAT(채팅 면접), VOICE(음성 면접), VIDEO(영상 면접)"),
                fieldWithPath("data.interviewMode")
                    .type(JsonFieldType.STRING)
                    .description("면접 모드: REAL(실전 면접 모드), GENERAL(일반/모의 면접 모드)"),
                fieldWithPath("data.job.jobId").type(JsonFieldType.NUMBER).description("직무 식별자"),
                fieldWithPath("data.job.jobName")
                    .type(JsonFieldType.STRING)
                    .description("직무 이름: BACK_END(백엔드), FRONT_END(프론트엔드), INFRA(인프라), AI(인공지능)"),
                fieldWithPath("data.job.jobNameKorean")
                    .type(JsonFieldType.STRING)
                    .description("직무 이름: 백엔드, 프론트엔드, 인프라, 인공지능"),
                fieldWithPath("data.job.jobDescription")
                    .type(JsonFieldType.STRING)
                    .description("직무 설명"),
                fieldWithPath("data.files[0].fileId")
                    .type(JsonFieldType.NUMBER)
                    .description("파일 식별자"),
                fieldWithPath("data.files[0].type")
                    .type(JsonFieldType.STRING)
                    .description("파일 유형: COVER_LETTER, RESUME, PORTFOLIO"),
                fieldWithPath("data.files[0].fileName")
                    .type(JsonFieldType.STRING)
                    .description("파일 이름"),
                fieldWithPath("data.files[0].s3FileUrl")
                    .type(JsonFieldType.STRING)
                    .description("파일 경로: {파일 유형}/{유저 식별자}/{timestamp}/{파일명}"))));
  }

  @Test
  @WithMockUser
  @DisplayName("필수 필드 누락 - 400 Bad Request")
  void create_interview_missing_required_fields() throws Exception {
    // given: 필수 필드 중 InterviewType 누락
    List<FileRequestDto> files = new ArrayList<>();
    String timestamp = String.valueOf(System.currentTimeMillis());

    files.add(
        new CoverLetterRequestDto(
            FileType.COVER_LETTER, "COVER_LETTER/1/" + timestamp + "/coverLetterName"));
    InterviewCreateRequestDto interviewCreateRequestDto =
        new InterviewCreateRequestDto(
            null, null, InterviewMethod.VIDEO, InterviewMode.REAL, 2L, files);
    String content = objectMapper.writeValueAsString(interviewCreateRequestDto);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/interview").contentType(MediaType.APPLICATION_JSON).content(content));

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("code").value(400))
        .andExpect(jsonPath("message").value("BAD_REQUEST"))
        .andExpect(jsonPath("data").value("interviewType: Interview Type is required"));

    // restdocs
    resultActions.andDo(
        document(
            "면접 정보 입력 - 필수 필드 누락(400 Bad Request)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드: 400"),
                fieldWithPath("message")
                    .type(JsonFieldType.STRING)
                    .description("응답 상태: BAD_REQUEST"),
                fieldWithPath("data")
                    .type(JsonFieldType.STRING)
                    .description("에러 메시지: {누락된 필드}: {누락된 필드} is required"))));
  }

  @Test
  @WithMockUser
  @DisplayName("존재하지 않는 직무 ID - 500 Internal Server Error")
  void create_interview_with_invalid_jobId() throws Exception {
    // given
    Long userId = 1L;
    List<FileRequestDto> files = new ArrayList<>();
    String timestamp = String.valueOf(System.currentTimeMillis());
    String accessToken = jwtUtil.generateAccessToken(1L);

    files.add(
        new CoverLetterRequestDto(
            FileType.COVER_LETTER, "COVER_LETTER/1/" + timestamp + "/coverLetterName"));

    InterviewCreateRequestDto interviewCreateRequestDto =
        new InterviewCreateRequestDto(
            "interview title",
            InterviewType.TECHNICAL,
            InterviewMethod.VIDEO,
            InterviewMode.REAL,
            999L, // 유효하지 않은 직무 ID
            files);

    MockCookie authCookie = new MockCookie("access_token", accessToken);

    String content = objectMapper.writeValueAsString(interviewCreateRequestDto);

    when(interviewService.createInterview(any(), eq(userId)))
        .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Job with ID 999 not found"));

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/interview")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie)
                .content(content));

    // then
    resultActions
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("code").value(500))
        .andExpect(jsonPath("message").value("INTERNAL_SERVER_ERROR"))
        .andExpect(jsonPath("data").value("Job with ID 999 not found"));

    // restdocs
    resultActions.andDo(
        document(
            "면접 정보 입력 - 존재하지 않는 직무 ID(500 Internal Server Error)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data").type(JsonFieldType.STRING).description("오류 메시지"))));
  }

  @Test
  @WithMockUser
  @DisplayName("서버 내부 오류 - 500 Internal Server Error")
  void create_interview_with_internal_server_error() throws Exception {
    // given
    Long userId = 1L;
    List<FileRequestDto> files = new ArrayList<>();
    String timestamp = String.valueOf(System.currentTimeMillis());
    String accessToken = jwtUtil.generateAccessToken(1L);

    files.add(
        new CoverLetterRequestDto(
            FileType.COVER_LETTER, "COVER_LETTER/1/" + timestamp + "/coverLetterName"));

    InterviewCreateRequestDto interviewCreateRequestDto =
        new InterviewCreateRequestDto(
            "interview title",
            InterviewType.TECHNICAL,
            InterviewMethod.VIDEO,
            InterviewMode.REAL,
            2L,
            files);
    String content = objectMapper.writeValueAsString(interviewCreateRequestDto);

    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(interviewService.createInterview(any(), eq(userId)))
        .thenThrow(
            new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Interview ID was not generated properly"));

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/interview")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie)
                .content(content));

    // then
    resultActions
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("code").value(500))
        .andExpect(jsonPath("message").value("INTERNAL_SERVER_ERROR"))
        .andExpect(jsonPath("data").value("Interview ID was not generated properly"));

    // restdocs
    resultActions.andDo(
        document(
            "면접 정보 입력 - 서버 내부 오류(500 Internal Server Error)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data").type(JsonFieldType.STRING).description("오류 메시지"))));
  }

  @Test
  @DisplayName("잘못된 데이터 형식 - 400 Bad Request")
  void create_interview_with_invalid_data_format() throws Exception {
    // given
    String invalidContent =
        "{\"interviewType\":\"INVALID_TYPE\",\"interviewMethod\":\"VIDEO\",\"interviewMode\":\"REAL\",\"jobId\":2}";

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/interview").contentType(MediaType.APPLICATION_JSON).content(invalidContent));

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("code").value(400))
        .andExpect(jsonPath("message").value("BAD_REQUEST"))
        .andExpect(jsonPath("data").value("Request body is missing or unreadable"));

    // restdocs
    resultActions.andDo(
        document(
            "면접 정보 입력 - 잘못된 데이터 형식(400 Bad Request)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data").type(JsonFieldType.STRING).description("에러 메시지"))));
  }
}
