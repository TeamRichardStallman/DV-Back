package org.richardstallman.dvback.domain.ticket.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;
import org.richardstallman.dvback.domain.ticket.service.TicketService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private TicketService ticketService;

  @Test
  @DisplayName("유저 이용권 정보 조회 - 성공")
  void getUserTicketInfo() throws Exception {
    // given
    Long userId = 1L;
    int totalBalance = 10;
    int realChatBalance = 1;
    int realVoiceBalance = 2;
    int generalChatBalance = 3;
    int generalVoiceBalance = 4;

    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto1 =
        new TicketTransactionDetailResponseDto(
            1L,
            1,
            TicketTransactionType.CHARGE,
            TicketTransactionType.CHARGE.getKoreanName(),
            TicketTransactionMethod.COUPON,
            TicketTransactionMethod.COUPON.getKoreanName(),
            InterviewMode.REAL,
            InterviewMode.REAL.getKoreanName(),
            InterviewAssetType.CHAT,
            InterviewAssetType.CHAT.getKoreanName(),
            "실전 채팅 면접 충전",
            LocalDateTime.now());
    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto2 =
        new TicketTransactionDetailResponseDto(
            2L,
            1,
            TicketTransactionType.USE,
            TicketTransactionType.USE.getKoreanName(),
            TicketTransactionMethod.CHAT,
            TicketTransactionMethod.CHAT.getKoreanName(),
            InterviewMode.REAL,
            InterviewMode.REAL.getKoreanName(),
            InterviewAssetType.CHAT,
            InterviewAssetType.CHAT.getKoreanName(),
            "실전 채팅 면접 사용",
            LocalDateTime.now());
    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto3 =
        new TicketTransactionDetailResponseDto(
            3L,
            1,
            TicketTransactionType.CHARGE,
            TicketTransactionType.CHARGE.getKoreanName(),
            TicketTransactionMethod.COUPON,
            TicketTransactionMethod.COUPON.getKoreanName(),
            InterviewMode.REAL,
            InterviewMode.REAL.getKoreanName(),
            InterviewAssetType.CHAT,
            InterviewAssetType.CHAT.getKoreanName(),
            "실전 채팅 면접 충전",
            LocalDateTime.now());
    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto4 =
        new TicketTransactionDetailResponseDto(
            4L,
            2,
            TicketTransactionType.CHARGE,
            TicketTransactionType.CHARGE.getKoreanName(),
            TicketTransactionMethod.COUPON,
            TicketTransactionMethod.COUPON.getKoreanName(),
            InterviewMode.REAL,
            InterviewMode.REAL.getKoreanName(),
            InterviewAssetType.VOICE,
            InterviewAssetType.VOICE.getKoreanName(),
            "실전 채팅 면접 충전",
            LocalDateTime.now());
    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto5 =
        new TicketTransactionDetailResponseDto(
            5L,
            3,
            TicketTransactionType.CHARGE,
            TicketTransactionType.CHARGE.getKoreanName(),
            TicketTransactionMethod.COUPON,
            TicketTransactionMethod.COUPON.getKoreanName(),
            InterviewMode.GENERAL,
            InterviewMode.GENERAL.getKoreanName(),
            InterviewAssetType.CHAT,
            InterviewAssetType.CHAT.getKoreanName(),
            "모의 채팅 면접 충전",
            LocalDateTime.now());
    TicketTransactionDetailResponseDto ticketTransactionDetailResponseDto6 =
        new TicketTransactionDetailResponseDto(
            6L,
            4,
            TicketTransactionType.CHARGE,
            TicketTransactionType.CHARGE.getKoreanName(),
            TicketTransactionMethod.COUPON,
            TicketTransactionMethod.COUPON.getKoreanName(),
            InterviewMode.GENERAL,
            InterviewMode.GENERAL.getKoreanName(),
            InterviewAssetType.VOICE,
            InterviewAssetType.VOICE.getKoreanName(),
            "모의 음성 면접 충전",
            LocalDateTime.now());
    List<TicketTransactionDetailResponseDto> ticketTransactionDetailResponseDtos =
        new ArrayList<>();
    ticketTransactionDetailResponseDtos.add(ticketTransactionDetailResponseDto1);
    ticketTransactionDetailResponseDtos.add(ticketTransactionDetailResponseDto2);
    ticketTransactionDetailResponseDtos.add(ticketTransactionDetailResponseDto3);
    ticketTransactionDetailResponseDtos.add(ticketTransactionDetailResponseDto4);
    ticketTransactionDetailResponseDtos.add(ticketTransactionDetailResponseDto5);
    ticketTransactionDetailResponseDtos.add(ticketTransactionDetailResponseDto6);
    TicketUserCountInfoDto ticketUserCountInfoDto =
        new TicketUserCountInfoDto(
            totalBalance,
            realChatBalance,
            realVoiceBalance,
            generalChatBalance,
            generalVoiceBalance);
    TicketUserInfoDto ticketUserInfoDto =
        new TicketUserInfoDto(ticketUserCountInfoDto, ticketTransactionDetailResponseDtos);

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(ticketService.getUserTicketInfo(eq(userId))).thenReturn(ticketUserInfoDto);
    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/ticket/user").cookie(authCookie).contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.userCountInfo.totalBalance").value(totalBalance))
        .andExpect(jsonPath("data.userCountInfo.realChatBalance").value(realChatBalance))
        .andExpect(jsonPath("data.userCountInfo.realVoiceBalance").value(realVoiceBalance))
        .andExpect(jsonPath("data.userCountInfo.generalChatBalance").value(generalChatBalance))
        .andExpect(jsonPath("data.userCountInfo.generalVoiceBalance").value(generalVoiceBalance))
        .andExpect(jsonPath("data.ticketTransactionDetails[0].ticketTransactionId").value(1));

    // restdocs
    resultActions.andDo(
        document(
            "유저 이용권 정보 조회 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Ticket API")
                    .summary("이용권 API")
                    .description("조회한 유저의 보유 이용권 수량과 이용권 거래 내역 최신순으로 조회")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.userCountInfo.totalBalance").description("총 보유 이용권 수"),
                        fieldWithPath("data.userCountInfo.realChatBalance")
                            .description("보유 실전 채팅 이용권 수"),
                        fieldWithPath("data.userCountInfo.realVoiceBalance")
                            .description("보유 실전 음성 이용권 수"),
                        fieldWithPath("data.userCountInfo.generalChatBalance")
                            .description("보유 모의 채팅 이용권 수"),
                        fieldWithPath("data.userCountInfo.generalVoiceBalance")
                            .description("보유 모의 음성 이용권 수"),
                        fieldWithPath("data.ticketTransactionDetails[].ticketTransactionId")
                            .description("이용권 거래 내역 식별자"),
                        fieldWithPath("data.ticketTransactionDetails[].amount")
                            .description("이용권 거래 내역 - 거래 수량"),
                        fieldWithPath("data.ticketTransactionDetails[].ticketTransactionType")
                            .description("이용권 거래 내역 - 이용권 거래 유형(CHARGE(충전), USE(사용))"),
                        fieldWithPath("data.ticketTransactionDetails[].ticketTransactionTypeKorean")
                            .description("이용권 거래 내역 - 이용권 거래 유형 한글"),
                        fieldWithPath("data.ticketTransactionDetails[].ticketTransactionMethod")
                            .description(
                                "이용권 거래 내역 - 이용권 거래 방식(COUPON(쿠폰), EVENT(이벤트), CHAT(채팅), VOICE(음성))"),
                        fieldWithPath(
                                "data.ticketTransactionDetails[].ticketTransactionMethodKorean")
                            .description("이용권 거래 내역 - 이용권 거래 방식 한글"),
                        fieldWithPath("data.ticketTransactionDetails[].interviewMode")
                            .description("이용권 거래 내역 - 면접 모드(REAL(실전), GENERAL(모의))"),
                        fieldWithPath("data.ticketTransactionDetails[].interviewModeKorean")
                            .description("이용권 거래 내역 - 면접 모드 한글"),
                        fieldWithPath("data.ticketTransactionDetails[].interviewAssetType")
                            .description("이용권 거래 내역 - 이용권 유형(CHAT(채팅), VOICE(음성))"),
                        fieldWithPath("data.ticketTransactionDetails[].interviewAssetTypeKorean")
                            .description("이용권 거래 내역 - 이용권 유형 한글"),
                        fieldWithPath("data.ticketTransactionDetails[].description")
                            .description(
                                "이용권 거래 내역 - 거래 설명(사용일 때: {면접 모드} {이용권 거래 방식} {이용권 거래 유형}) / 충전일 때: {면접 모드} {이용권 유형} {이용권 거래 방식} {이용권 거래 유형} ex)\"실전 채팅 쿠폰 충전\", \"모의 채팅 사용\""),
                        fieldWithPath("data.ticketTransactionDetails[].generatedAt")
                            .description("이용권 거래 내역 - 거래 일시"))
                    .build())));
  }

  @Test
  @DisplayName("유저 보유 이용권 수량만 조회 - 성공")
  void getUserTicketCountInfo() throws Exception {
    // given
    Long userId = 1L;
    int totalBalance = 10;
    int realChatBalance = 1;
    int realVoiceBalance = 2;
    int generalChatBalance = 3;
    int generalVoiceBalance = 4;

    TicketUserCountInfoDto ticketUserCountInfoDto =
        new TicketUserCountInfoDto(
            totalBalance,
            realChatBalance,
            realVoiceBalance,
            generalChatBalance,
            generalVoiceBalance);

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(ticketService.getUserCountInfo(eq(userId))).thenReturn(ticketUserCountInfoDto);
    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/ticket/user/count").cookie(authCookie).contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.totalBalance").value(totalBalance))
        .andExpect(jsonPath("data.realChatBalance").value(realChatBalance))
        .andExpect(jsonPath("data.realVoiceBalance").value(realVoiceBalance))
        .andExpect(jsonPath("data.generalChatBalance").value(generalChatBalance))
        .andExpect(jsonPath("data.generalVoiceBalance").value(generalVoiceBalance));

    // restdocs
    resultActions.andDo(
        document(
            "유저 보유 이용권 수량만 조회 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Ticket API")
                    .summary("이용권 API")
                    .description("조회한 유저의 보유 이용권 수량만 조회")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.totalBalance").description("총 보유 이용권 수"),
                        fieldWithPath("data.realChatBalance").description("보유 실전 채팅 이용권 수"),
                        fieldWithPath("data.realVoiceBalance").description("보유 실전 음성 이용권 수"),
                        fieldWithPath("data.generalChatBalance").description("보유 모의 채팅 이용권 수"),
                        fieldWithPath("data.generalVoiceBalance").description("보유 모의 음성 이용권 수"))
                    .build())));
  }
}
