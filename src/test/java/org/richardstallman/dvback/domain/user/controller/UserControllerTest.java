package org.richardstallman.dvback.domain.user.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;
import org.richardstallman.dvback.domain.ticket.service.TicketService;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.request.UserUpdateRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.service.UserService;
import org.richardstallman.dvback.global.jwt.JwtUtil;
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtUtil jwtUtil;

  @MockBean private UserService userService;
  @MockBean private TicketService ticketService;
  @MockBean private RefreshTokenRepository refreshTokenRepository;

  @Test
  @WithMockUser
  @DisplayName("유저 정보 생성 - 성공")
  void create_user_info_success() throws Exception {
    // given
    UserRequestDto userRequestDto =
        new UserRequestDto(
            "https://example.com/image.jpg",
            "김수현",
            "suhyun",
            "dodam",
            LocalDate.of(1990, 1, 1),
            CommonConstants.Gender.WOMAN);

    String userInfoJson = objectMapper.writeValueAsString(userRequestDto);

    UserResponseDto userResponseDto =
        new UserResponseDto(
            1L,
            "12345",
            "example@test.com",
            "suhyun",
            "김수현",
            "dodam",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            LocalDate.of(1990, 1, 1));

    when(userService.createUserInfo(any(Long.class), any(UserRequestDto.class)))
        .thenReturn(userResponseDto);

    String accessToken = jwtUtil.generateAccessToken(1L);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/user/info")
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userInfoJson));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.nickname").value("dodam"))
        .andExpect(jsonPath("$.data.email").value("example@test.com"));

    // restdocs
    resultActions.andDo(
        document(
            "유저 정보 생성 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저 API")
                    .requestFields(
                        fieldWithPath("s3ProfileImageObjectKey").description("프로필 이미지 오브젝트 키"),
                        fieldWithPath("name").description("유저 이름"),
                        fieldWithPath("username").description("유저네임"),
                        fieldWithPath("nickname").description("유저 닉네임"),
                        fieldWithPath("birthdate").description("생년월일"),
                        fieldWithPath("gender").description("성별"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.userId").description("유저 ID"),
                        fieldWithPath("data.socialId").description("소셜 ID"),
                        fieldWithPath("data.email").description("이메일"),
                        fieldWithPath("data.username").description("유저네임"),
                        fieldWithPath("data.name").description("유저 이름"),
                        fieldWithPath("data.nickname").description("유저 닉네임"),
                        fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.leave").description("탈퇴 여부"),
                        fieldWithPath("data.gender").description("성별"),
                        fieldWithPath("data.birthdate").description("생년월일"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저 정보 수정 - 성공")
  void update_user_info_success() throws Exception {
    // given
    UserUpdateRequestDto userUpdateRequestDto =
        new UserUpdateRequestDto(
            "https://example.com/image.jpg",
            "김수현",
            "ddodam",
            LocalDate.of(1990, 1, 1),
            CommonConstants.Gender.WOMAN);

    String userInfoJson = objectMapper.writeValueAsString(userUpdateRequestDto);

    UserResponseDto userResponseDto =
        new UserResponseDto(
            1L,
            "12345",
            "example@test.com",
            "suhyun",
            "김수현",
            "ddodam",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            LocalDate.of(1990, 1, 1));

    when(userService.updateUserInfo(any(Long.class), any(UserUpdateRequestDto.class)))
        .thenReturn(userResponseDto);

    String accessToken = jwtUtil.generateAccessToken(1L);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            put("/user/info")
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userInfoJson));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.nickname").value("ddodam"))
        .andExpect(jsonPath("$.data.email").value("example@test.com"));

    // restdocs
    resultActions.andDo(
        document(
            "유저 정보 수정 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저 API")
                    .requestFields(
                        fieldWithPath("s3ProfileImageObjectKey").description("프로필 이미지 오브젝트 키"),
                        fieldWithPath("name").description("유저 이름"),
                        fieldWithPath("nickname").description("유저 닉네임"),
                        fieldWithPath("birthdate").description("생년월일"),
                        fieldWithPath("gender").description("성별"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.userId").description("유저 ID"),
                        fieldWithPath("data.socialId").description("소셜 ID"),
                        fieldWithPath("data.email").description("이메일"),
                        fieldWithPath("data.username").description("유저네임"),
                        fieldWithPath("data.name").description("유저 이름"),
                        fieldWithPath("data.nickname").description("유저 닉네임"),
                        fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.leave").description("탈퇴 여부"),
                        fieldWithPath("data.gender").description("성별"),
                        fieldWithPath("data.birthdate").description("생년월일"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("로그아웃 - 성공")
  void logout_success() throws Exception {
    // Mocked Cookie
    String accessToken = jwtUtil.generateAccessToken(1L);
    String refreshToken = jwtUtil.generateRefreshToken(1L);
    MockCookie accessTokenCookie = new MockCookie("access_token", accessToken);
    MockCookie refreshTokenCookie = new MockCookie("refresh_token", refreshToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            post("/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(accessTokenCookie, refreshTokenCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Logged out successfully"));

    // restdocs
    resultActions.andDo(
        document("로그아웃 - 성공", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저 정보 조회 - 성공")
  void get_user_info_success() throws Exception {
    // given
    Long userId = 1L;
    UserResponseDto userResponseDto =
        new UserResponseDto(
            userId,
            "12345",
            "example@test.com",
            "suhyun",
            "김수현",
            "왕감자",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            LocalDate.of(1990, 1, 1));

    when(userService.getUserInfo(userId)).thenReturn(userResponseDto);

    // Mocked Cookie for authorization
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/info").contentType(MediaType.APPLICATION_JSON).cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.userId").value(userId))
        .andExpect(jsonPath("$.data.nickname").value("왕감자"))
        .andExpect(jsonPath("$.data.email").value("example@test.com"));

    // restdocs
    resultActions.andDo(
        document(
            "유저 정보 조회 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저 API")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.userId").description("유저 ID"),
                        fieldWithPath("data.socialId").description("소셜 ID"),
                        fieldWithPath("data.email").description("이메일"),
                        fieldWithPath("data.username").description("유저 네임"),
                        fieldWithPath("data.name").description("유저 이름"),
                        fieldWithPath("data.nickname").description("유저 닉네임"),
                        fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.leave").description("탈퇴 여부"),
                        fieldWithPath("data.gender").description("성별"),
                        fieldWithPath("data.birthdate").description("생년월일"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("인증 상태 확인 성공")
  void isAuthenticated_success() throws Exception {
    // given
    Long userId = 1L; // Mock user ID
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/authenticated").contentType(MediaType.APPLICATION_JSON).cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "로그인 여부 확인 - 인증 상태 확인 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("현재 사용자 인증 상태 확인")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("인증 여부 (true: 인증됨, false: 인증되지 않음)"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("토큰 없음으로 인증 실패")
  void isAuthenticated_failure_noToken() throws Exception {
    // when
    ResultActions resultActions =
        mockMvc.perform(get("/user/authenticated").contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(false));

    // restdocs
    resultActions.andDo(
        document(
            "로그인 여부 확인 - 인증 실패 (토큰 없음)",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("현재 사용자 인증 상태 확인")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("인증 여부 (true: 인증됨, false: 인증되지 않음)"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저네임 중복 확인 - 중복있음")
  void validate_username_success() throws Exception {
    // given
    String username = "suhyun";
    when(userService.existsByUsername(username)).thenReturn(true);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/validate-username")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "유저네임 중복 확인 - 중복있음",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저네임 중복 확인 API")
                    .description("주어진 유저네임이 중복인지 확인합니다.")
                    .queryParameters(parameterWithName("username").description("중복 확인할 유저네임"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("유저네임 중복 여부 (true: 중복, false: 사용 가능)"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("유저네임 중복 확인 - 중복 없음")
  void validate_username_no_duplicate() throws Exception {
    // given
    String username = "newUser";
    when(userService.existsByUsername(username)).thenReturn(false);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/validate-username")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data").value(false));

    // restdocs
    resultActions.andDo(
        document(
            "유저네임 중복 확인 - 중복 없음",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저네임 중복 확인 API")
                    .description("주어진 유저네임이 중복인지 확인합니다.")
                    .queryParameters(parameterWithName("username").description("중복 확인할 유저네임"))
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("유저네임 중복 여부 (true: 중복, false: 사용 가능)"))
                    .build())));
  }

  @Test
  @DisplayName("마이페이지 - 유저 정보 + 유저 보유 이용권 수량 조회 - 성공")
  void get_user_info_and_ticket_amount_in_my_page() throws Exception {
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

    UserResponseDto userResponseDto =
        new UserResponseDto(
            userId,
            "12345",
            "example@test.com",
            "suhyun",
            "김수현",
            "왕감자",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            LocalDate.of(1990, 1, 1));

    when(userService.getUserInfo(userId)).thenReturn(userResponseDto);
    when(ticketService.getUserCountInfo(userId)).thenReturn(ticketUserCountInfoDto);

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            get("/user/my-page").cookie(authCookie).contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.user.userId").value(userId))
        .andExpect(jsonPath("$.data.user.username").value("suhyun"))
        .andExpect(jsonPath("$.data.user.email").value("example@test.com"))
        .andExpect(jsonPath("$.data.ticketInfo.totalBalance").value(totalBalance))
        .andExpect(jsonPath("$.data.ticketInfo.realChatBalance").value(realChatBalance))
        .andExpect(jsonPath("$.data.ticketInfo.realVoiceBalance").value(realVoiceBalance))
        .andExpect(jsonPath("$.data.ticketInfo.generalChatBalance").value(generalChatBalance))
        .andExpect(jsonPath("$.data.ticketInfo.generalVoiceBalance").value(generalVoiceBalance));

    // restdocs
    resultActions.andDo(
        document(
            "마이페이지 - 유저 정보 + 유저 보유 이용권 수량 조회 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("User API")
                    .summary("유저 API")
                    .description("마이페이지 - 유저 정보 + 유저가 보유한 이용권 수량 조회")
                    .responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data.user.userId").description("유저 ID"),
                        fieldWithPath("data.user.socialId").description("소셜 ID"),
                        fieldWithPath("data.user.email").description("이메일"),
                        fieldWithPath("data.user.username").description("유저네임"),
                        fieldWithPath("data.user.name").description("유저 이름"),
                        fieldWithPath("data.user.nickname").description("유저 닉네임"),
                        fieldWithPath("data.user.s3ProfileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.user.leave").description("탈퇴 여부"),
                        fieldWithPath("data.user.gender").description("성별"),
                        fieldWithPath("data.user.birthdate").description("생년월일"),
                        fieldWithPath("data.ticketInfo.totalBalance").description("총 보유 이용권 수"),
                        fieldWithPath("data.ticketInfo.realChatBalance")
                            .description("보유 실전 채팅 이용권 수"),
                        fieldWithPath("data.ticketInfo.realVoiceBalance")
                            .description("보유 실전 음성 이용권 수"),
                        fieldWithPath("data.ticketInfo.generalChatBalance")
                            .description("보유 모의 채팅 이용권 수"),
                        fieldWithPath("data.ticketInfo.generalVoiceBalance")
                            .description("보유 모의 음성 이용권 수"))
                    .build())));
  }
}
