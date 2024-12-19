package org.richardstallman.dvback.domain.interview.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewAddFileRequestDto;
import org.richardstallman.dvback.domain.interview.domain.request.InterviewCreateRequestDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewAddFileResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewCreateResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationListResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewEvaluationResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewListResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketResponseDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;
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
    List<TicketTransactionDetailResponseDto> ticketTransactionDetailResponseDtos =
        new ArrayList<>();
    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto1 =
        new TicketTransactionDetailResponseDto(
            1L,
            1,
            TicketTransactionType.USE,
            TicketTransactionType.USE.getKoreanName(),
            TicketTransactionMethod.CHAT,
            TicketTransactionMethod.CHAT.getKoreanName(),
            InterviewMode.REAL,
            InterviewMode.REAL.getKoreanName(),
            InterviewAssetType.CHAT,
            InterviewAssetType.CHAT.getKoreanName(),
            "채팅 면접 사용",
            LocalDateTime.now());

    TicketResponseDto ticketResponseDto =
        new TicketResponseDto(10, 1, 2, 3, 4, ticketTransactionDetailResponseDto1);

    TicketUserCountInfoDto ticketUserCountInfoDto = new TicketUserCountInfoDto(10, 1, 2, 3, 4);
    TicketUserInfoDto ticketUserInfoDto =
        new TicketUserInfoDto(ticketUserCountInfoDto, ticketTransactionDetailResponseDtos);

    files.add(
        new CoverLetterRequestDto(
            FileType.COVER_LETTER, "COVER_LETTER/1/" + timestamp + "/coverLetterName"));

    InterviewCreateRequestDto interviewCreateRequestDto =
        new InterviewCreateRequestDto(
            "면접 제목",
            InterviewType.TECHNICAL,
            InterviewMethod.VOICE,
            InterviewMode.REAL,
            2L,
            5,
            files);

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
                InterviewMethod.VOICE,
                InterviewMode.REAL,
                5,
                JobDomain.builder()
                    .jobId(1L)
                    .jobName("BACK_END")
                    .jobNameKorean("백엔드")
                    .jobDescription("백엔드 직무입니다.")
                    .build(),
                fileResponseDtos,
                ticketResponseDto));
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
        .andExpect(jsonPath("data.interviewMethod").value("VOICE"))
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
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("면접 API")
                    .requestFields(
                        fieldWithPath("interviewTitle")
                            .type(JsonFieldType.STRING)
                            .description("면접 제목"),
                        fieldWithPath("interviewType")
                            .type(JsonFieldType.STRING)
                            .description("면접 유형: TECHNICAL(기술 면접), PERSONAL(인성 면접)"),
                        fieldWithPath("interviewMethod")
                            .type(JsonFieldType.STRING)
                            .description("면접 방식: CHAT(채팅 면접), VOICE(음성 면접), VIDEO(영상 면접)"),
                        fieldWithPath("interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("면접 모드: REAL(실전 면접 모드), GENERAL(일반/모의 면접 모드)"),
                        fieldWithPath("questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 질문 개수"),
                        fieldWithPath("jobId").type(JsonFieldType.NUMBER).description("직무 식별자"),
                        fieldWithPath("files[0].type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형: COVER_LETTER, RESUME, PORTFOLIO"),
                        fieldWithPath("files[0].filePath")
                            .type(JsonFieldType.STRING)
                            .description("파일 경로: {파일 유형}/{유저 식별자}/{timestamp}/{파일명}"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
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
                        fieldWithPath("data.questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 질문 개수"),
                        fieldWithPath("data.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description(
                                "직무 이름: BACK_END(백엔드), FRONT_END(프론트엔드), INFRA(인프라), AI(인공지능)"),
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
                            .description("파일 경로: {파일 유형}/{유저 식별자}/{timestamp}/{파일명}"),
                        fieldWithPath("data.ticket.totalBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("이용권 사용 후 잔여 이용권 수"),
                        fieldWithPath("data.ticket.realChatBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("(실전 면접 시) 이용권 사용 후 잔여 채팅 이용권 수"),
                        fieldWithPath("data.ticket.realVoiceBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("(실전 면접 시) 이용권 사용 후 잔여 음성 이용권 수"),
                        fieldWithPath("data.ticket.generalChatBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("(모의 면접 시) 이용권 사용 후 잔여 채팅 이용권 수"),
                        fieldWithPath("data.ticket.generalVoiceBalance")
                            .type(JsonFieldType.NUMBER)
                            .description("(모의 면접 시) 이용권 사용 후 잔여 음성 이용권 수"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.ticketTransactionId")
                            .type(JsonFieldType.NUMBER)
                            .description("이용권 사용 내역 식별자"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.amount")
                            .type(JsonFieldType.NUMBER)
                            .description("이용권 사용 내역 사용 수량"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.ticketTransactionType")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 타입: USE(사용), CHARGE(충전)"),
                        fieldWithPath(
                                "data.ticket.ticketTransactionDetail.ticketTransactionTypeKorean")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 타입 한글"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.ticketTransactionMethod")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 방식: CHAT(채팅), VOICE(음성)"),
                        fieldWithPath(
                                "data.ticket.ticketTransactionDetail.ticketTransactionMethodKorean")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 방식 한글"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("이용권 면접 모드: REAL(실전), GENERAL(모의)"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.interviewModeKorean")
                            .type(JsonFieldType.STRING)
                            .description("이용권 면접 모드 한글"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.interviewAssetType")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 타입: CHAT(채팅), VOICE(음성)"),
                        fieldWithPath(
                                "data.ticket.ticketTransactionDetail.interviewAssetTypeKorean")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 타입 한글"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.description")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 설명"),
                        fieldWithPath("data.ticket.ticketTransactionDetail.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("이용권 사용 내역 생성 일시"))
                    .build())));
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
            null, null, InterviewMethod.VOICE, InterviewMode.REAL, 2L, 5, files);
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
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("면접 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드: 400"),
                        fieldWithPath("message")
                            .type(JsonFieldType.STRING)
                            .description("응답 상태: BAD_REQUEST"),
                        fieldWithPath("data")
                            .type(JsonFieldType.STRING)
                            .description("에러 메시지: {누락된 필드}: {누락된 필드} is required"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("면접 방식이 VOICE일 때 면접 파일 s3 업로드 후 따로 저장 위한 면접 수정 - 성공")
  void update_interview_when_interview_method_is_voice_and_create_file() throws Exception {
    // given
    Long interviewId = 2L;
    String interviewTitle = "2024-12-15_TECHNICAL_CHAT_REAL_면접";
    InterviewStatus interviewStatus = InterviewStatus.FILE_UPLOADED;
    InterviewType interviewType = InterviewType.TECHNICAL;
    InterviewMethod interviewMethod = InterviewMethod.CHAT;
    InterviewMode interviewMode = InterviewMode.REAL;
    int questionCount = 5;
    JobDomain jobDomain =
        JobDomain.builder()
            .jobId(1L)
            .jobName("BACK_END")
            .jobNameKorean("백엔드")
            .jobDescription("백엔드 직무입니다.")
            .build();
    FileResponseDto file = new FileResponseDto(1L, FileType.COVER_LETTER, "fileName", "s3FileUrl");

    InterviewAddFileResponseDto interviewAddFileResponseDto =
        new InterviewAddFileResponseDto(
            interviewId,
            interviewTitle,
            interviewStatus,
            interviewType,
            interviewMethod,
            interviewMode,
            questionCount,
            jobDomain,
            file);

    when(interviewService.addInterviewFile(any())).thenReturn(interviewAddFileResponseDto);

    InterviewAddFileRequestDto interviewAddFileRequestDto =
        new InterviewAddFileRequestDto(
            interviewId, new CoverLetterRequestDto(FileType.COVER_LETTER, file.getS3FileUrl()));

    String content = objectMapper.writeValueAsString(interviewAddFileRequestDto);

    // when
    ResultActions resultActions =
        mockMvc.perform(put("/interview").contentType(MediaType.APPLICATION_JSON).content(content));

    // then
    resultActions.andExpect(status().isOk());

    // restdocs
    resultActions.andDo(
        document(
            "면접 방식이 VOICE일 때 면접 파일 s3 업로드 후 따로 저장 위한 면접 수정 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("면접 파일 정보 추가 위한 면접 수정 API")
                    .requestFields(
                        fieldWithPath("interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("coverLetter.type")
                            .type(JsonFieldType.STRING)
                            .description("면접 파일 유형: COVER_LETTER"),
                        fieldWithPath("coverLetter.filePath")
                            .type(JsonFieldType.STRING)
                            .description("s3 업로드 object key"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
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
                        fieldWithPath("data.questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 질문 개수"),
                        fieldWithPath("data.job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.job.jobName")
                            .type(JsonFieldType.STRING)
                            .description(
                                "직무 이름: BACK_END(백엔드), FRONT_END(프론트엔드), INFRA(인프라), AI(인공지능)"),
                        fieldWithPath("data.job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름: 백엔드, 프론트엔드, 인프라, 인공지능"),
                        fieldWithPath("data.job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.file.fileId")
                            .type(JsonFieldType.NUMBER)
                            .description("파일 식별자"),
                        fieldWithPath("data.file.type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형: COVER_LETTER, RESUME, PORTFOLIO"),
                        fieldWithPath("data.file.fileName")
                            .type(JsonFieldType.STRING)
                            .description("파일 이름"),
                        fieldWithPath("data.file.s3FileUrl")
                            .type(JsonFieldType.STRING)
                            .description("파일 경로"))
                    .build())));
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
            InterviewMethod.VOICE,
            InterviewMode.REAL,
            999L, // 유효하지 않은 직무 ID
            5,
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
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("면접 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.STRING).description("오류 메시지"))
                    .build())));
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
            InterviewMethod.VOICE,
            InterviewMode.REAL,
            2L,
            5,
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
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("면접 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.STRING).description("오류 메시지"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("잘못된 데이터 형식 - 400 Bad Request")
  void create_interview_with_invalid_data_format() throws Exception {
    // given
    String invalidContent =
        "{\"interviewType\":\"INVALID_TYPE\",\"interviewMethod\":\"VOICE\",\"interviewMode\":\"REAL\",\"jobId\":2}";

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
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("면접 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data").type(JsonFieldType.STRING).description("에러 메시지"))
                    .build())));
  }

  @Test
  @DisplayName("마이페이지 - 면접 내역 조회 - 성공")
  void getMyPageInterviews() throws Exception {
    // given
    Long userId = 1L;
    String timestamp = String.valueOf(System.currentTimeMillis());
    List<FileResponseDto> files =
        List.of(
            new FileResponseDto(
                1L,
                FileType.COVER_LETTER,
                "coverLetterName",
                "COVER_LETTER/1/" + timestamp + "/coverLetterName"));
    List<FileResponseDto> files2 =
        List.of(
            new FileResponseDto(
                2L,
                FileType.COVER_LETTER,
                "coverLetterName2",
                "COVER_LETTER/1/" + timestamp + "/coverLetterName2"));
    List<FileResponseDto> files3 =
        List.of(
            new FileResponseDto(
                3L,
                FileType.COVER_LETTER,
                "coverLetterName3",
                "COVER_LETTER/1/" + timestamp + "/coverLetterName3"));

    InterviewResponseDto interview1 =
        new InterviewResponseDto(
            1L,
            "면접 제목",
            InterviewStatus.READY,
            InterviewType.TECHNICAL,
            InterviewMethod.VOICE,
            InterviewMode.REAL,
            5,
            JobDomain.builder()
                .jobId(2L)
                .jobName("BACK_END")
                .jobNameKorean("백엔드")
                .jobDescription("백엔드 직무입니다.")
                .build(),
            files);

    InterviewResponseDto interview2 =
        new InterviewResponseDto(
            2L,
            "면접 제목 2",
            InterviewStatus.READY,
            InterviewType.PERSONAL,
            InterviewMethod.VOICE,
            InterviewMode.REAL,
            5,
            JobDomain.builder()
                .jobId(3L)
                .jobName("FRONT_END")
                .jobNameKorean("프론트엔드")
                .jobDescription("프론트엔드 직무입니다.")
                .build(),
            files2);

    InterviewResponseDto interview3 =
        new InterviewResponseDto(
            3L,
            "면접 제목 3",
            InterviewStatus.READY,
            InterviewType.TECHNICAL,
            InterviewMethod.CHAT,
            InterviewMode.REAL,
            5,
            JobDomain.builder()
                .jobId(4L)
                .jobName("INFRA")
                .jobNameKorean("인프라")
                .jobDescription("인프라 직무입니다.")
                .build(),
            files3);

    InterviewResponseDto interview4 =
        new InterviewResponseDto(
            4L,
            "면접 제목 4",
            InterviewStatus.READY,
            InterviewType.TECHNICAL,
            InterviewMethod.CHAT,
            InterviewMode.GENERAL,
            5,
            JobDomain.builder()
                .jobId(5L)
                .jobName("AI")
                .jobNameKorean("인공지능")
                .jobDescription("인공지능 직무입니다.")
                .build(),
            new ArrayList<>());

    List<InterviewResponseDto> interviewList =
        List.of(interview1, interview2, interview3, interview4);
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(interviewService.getInterviewsByUserId(userId))
        .thenReturn(new InterviewListResponseDto(interviewList));

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/interview").cookie(authCookie).contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.interviews[0].interviewId").value(1))
        .andExpect(jsonPath("data.interviews[0].interviewTitle").value("면접 제목"))
        .andExpect(jsonPath("data.interviews[0].interviewStatus").value("READY"))
        .andExpect(jsonPath("data.interviews[0].interviewType").value("TECHNICAL"))
        .andExpect(jsonPath("data.interviews[0].interviewMethod").value("VOICE"))
        .andExpect(jsonPath("data.interviews[0].interviewMode").value("REAL"))
        .andExpect(jsonPath("data.interviews[0].job.jobId").value(2))
        .andExpect(jsonPath("data.interviews[0].job.jobName").value("BACK_END"))
        .andExpect(jsonPath("data.interviews[1].interviewId").value(2))
        .andExpect(jsonPath("data.interviews[1].interviewTitle").value("면접 제목 2"))
        .andExpect(jsonPath("data.interviews[1].interviewType").value("PERSONAL"))
        .andExpect(jsonPath("data.interviews[1].job.jobNameKorean").value("프론트엔드"))
        .andExpect(jsonPath("data.interviews[2].interviewId").value(3))
        .andExpect(jsonPath("data.interviews[2].interviewTitle").value("면접 제목 3"))
        .andExpect(jsonPath("data.interviews[3].interviewTitle").value("면접 제목 4"));

    // restdocs
    resultActions.andDo(
        document(
            "마이페이지 - 면접 내역 조회 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("면접 내역 조회 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.interviews[].interviewId")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 식별자"),
                        fieldWithPath("data.interviews[].interviewTitle")
                            .type(JsonFieldType.STRING)
                            .description("면접 제목"),
                        fieldWithPath("data.interviews[].interviewStatus")
                            .type(JsonFieldType.STRING)
                            .description("면접 상태"),
                        fieldWithPath("data.interviews[].interviewType")
                            .type(JsonFieldType.STRING)
                            .description("면접 유형"),
                        fieldWithPath("data.interviews[].interviewMethod")
                            .type(JsonFieldType.STRING)
                            .description("면접 방식"),
                        fieldWithPath("data.interviews[].interviewMode")
                            .type(JsonFieldType.STRING)
                            .description("면접 모드"),
                        fieldWithPath("data.interviews[].questionCount")
                            .type(JsonFieldType.NUMBER)
                            .description("면접 질문 개수"),
                        fieldWithPath("data.interviews[].job.jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 식별자"),
                        fieldWithPath("data.interviews[].job.jobName")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름"),
                        fieldWithPath("data.interviews[].job.jobNameKorean")
                            .type(JsonFieldType.STRING)
                            .description("직무 이름 (한글)"),
                        fieldWithPath("data.interviews[].job.jobDescription")
                            .type(JsonFieldType.STRING)
                            .description("직무 설명"),
                        fieldWithPath("data.interviews[].files[]")
                            .type(JsonFieldType.ARRAY)
                            .description("파일 목록"),
                        fieldWithPath("data.interviews[].files[].fileId")
                            .type(JsonFieldType.NUMBER)
                            .description("파일 식별자"),
                        fieldWithPath("data.interviews[].files[].type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형"),
                        fieldWithPath("data.interviews[].files[].fileName")
                            .type(JsonFieldType.STRING)
                            .description("파일 이름"),
                        fieldWithPath("data.interviews[].files[].s3FileUrl")
                            .type(JsonFieldType.STRING)
                            .description("파일 경로"))
                    .build())));
  }

  @Test
  @DisplayName("마이페이지 - 면접 평가 조회 위한 면접 정보 목록(면접 식별자, 면접 제목) 조회 - 성공")
  void getMyPageInterviewInfoListForInterviewEvaluation() throws Exception {
    // given
    Long userId = 1L;
    List<InterviewEvaluationResponseDto> interviewEvaluationResponseDtos = new ArrayList<>();
    interviewEvaluationResponseDtos.add(new InterviewEvaluationResponseDto(1L, "241101_백엔드_모의"));
    interviewEvaluationResponseDtos.add(new InterviewEvaluationResponseDto(2L, "241103_프론트엔드_실전"));
    interviewEvaluationResponseDtos.add(new InterviewEvaluationResponseDto(4L, "241105_클라우드_실전"));
    interviewEvaluationResponseDtos.add(new InterviewEvaluationResponseDto(7L, "241105_인공지능_모의"));

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(interviewService.getInterviewsByUserIdForEvaluation(userId))
        .thenReturn(new InterviewEvaluationListResponseDto(interviewEvaluationResponseDtos));

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/interview/evaluation")
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.interviews[0].interviewTitle").value("241101_백엔드_모의"))
        .andExpect(jsonPath("data.interviews[0].interviewId").value(1))
        .andExpect(jsonPath("data.interviews[1].interviewTitle").value("241103_프론트엔드_실전"))
        .andExpect(jsonPath("data.interviews[1].interviewId").value(2))
        .andExpect(jsonPath("data.interviews[3].interviewTitle").value("241105_인공지능_모의"))
        .andExpect(jsonPath("data.interviews[3].interviewId").value(7))
        .andExpect(jsonPath("data.interviews[2].interviewTitle").value("241105_클라우드_실전"))
        .andExpect(jsonPath("data.interviews[2].interviewId").value(4));

    // restdocs
    resultActions.andDo(
        document(
            "마이페이지 - 면접 평가 조회 위한 면접 정보 목록(면접 식별자, 면접 제목) 조회 - 성공",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Evaluation API")
                    .summary("평가 API")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.interviews[0].interviewTitle").description("면접 제목"),
                        fieldWithPath("data.interviews[0].interviewId").description("면접 식별자"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("인터뷰 소유 여부 확인 - 사용자가 소유자인 경우 true 반환")
  void checkInterviewOwner() throws Exception {
    // given
    Long userId = 1L;
    Long interviewId = 100L;
    boolean isOwner = true;

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(interviewService.checkInterviewOwner(userId, interviewId)).thenReturn(isOwner);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/interview/{interviewId}/owner", interviewId)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "인터뷰 소유 여부 확인 - 사용자가 소유자인 경우 true 반환",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Interview API")
                    .summary("인터뷰 소유 여부 확인")
                    .description("주어진 인터뷰 ID에 대해 사용자가 소유자인지 확인합니다.")
                    .pathParameters(parameterWithName("interviewId").description("확인할 인터뷰의 식별자"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("소유 여부 (true: 소유자, false: 소유자가 아님)"))
                    .build())));
  }
}
