package org.richardstallman.dvback.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
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
  @MockBean private RefreshTokenRepository refreshTokenRepository;

  @Test
  @WithMockUser
  @DisplayName("유저 정보 업데이트 - 성공")
  void update_user_info_success() throws Exception {
    // given
    UserRequestDto userRequestDto =
        new UserRequestDto("왕감자", new Date(), CommonConstants.Gender.WOMAN);
    String content = objectMapper.writeValueAsString(userRequestDto);
    String accessToken = jwtUtil.generateAccessToken(1L);

    UserResponseDto userResponseDto =
        new UserResponseDto(
            1L,
            "12345",
            "example@test.com",
            "김수현",
            "왕감자",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            new Date());

    when(userService.updateUserInfo(any(Long.class), any(UserRequestDto.class)))
        .thenReturn(userResponseDto);

    // Mocked Cookie
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            put("/user/info")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie)
                .content(content));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.nickname").value("왕감자"))
        .andExpect(jsonPath("$.data.email").value("example@test.com"));

    // restdocs
    resultActions.andDo(
        document(
            "유저 정보 업데이트 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("nickname").description("유저의 새로운 닉네임"),
                fieldWithPath("birthdate").description("유저의 생년월일"),
                fieldWithPath("gender").description("유저의 성별")),
            responseFields(
                fieldWithPath("code").description("응답 코드"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data.userId").description("유저 ID"),
                fieldWithPath("data.socialId").description("소셜 ID"),
                fieldWithPath("data.email").description("이메일"),
                fieldWithPath("data.name").description("유저 이름"),
                fieldWithPath("data.nickname").description("유저 닉네임"),
                fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL"),
                fieldWithPath("data.leave").description("탈퇴 여부"),
                fieldWithPath("data.gender").description("성별"),
                fieldWithPath("data.birthdate").description("생년월일"))));
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
            "김수현",
            "왕감자",
            "https://example.com/image.jpg",
            false,
            CommonConstants.Gender.WOMAN,
            new Date());

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
            responseFields(
                fieldWithPath("code").description("응답 코드"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data.userId").description("유저 ID"),
                fieldWithPath("data.socialId").description("소셜 ID"),
                fieldWithPath("data.email").description("이메일"),
                fieldWithPath("data.name").description("유저 이름"),
                fieldWithPath("data.nickname").description("유저 닉네임"),
                fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL"),
                fieldWithPath("data.leave").description("탈퇴 여부"),
                fieldWithPath("data.gender").description("성별"),
                fieldWithPath("data.birthdate").description("생년월일"))));
  }

  @Test
  @WithMockUser
  @DisplayName("유저 탈퇴 - 성공")
  void deactivate_user_success() throws Exception {
    // given
    Long userId = 1L;
    UserResponseDto userResponseDto =
        new UserResponseDto(
            userId, null, "withdrawn@example.com", "탈퇴한 회원", "탈퇴한 회원", null, true, null, null);

    when(userService.deactivateUser(userId)).thenReturn(userResponseDto);

    // Mocked Cookie for authorization
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            delete("/user/leave").contentType(MediaType.APPLICATION_JSON).cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("SUCCESS"))
        .andExpect(jsonPath("$.data.userId").value(userId))
        .andExpect(jsonPath("$.data.nickname").value("탈퇴한 회원"))
        .andExpect(jsonPath("$.data.email").value("withdrawn@example.com"))
        .andExpect(jsonPath("$.data.leave").value(true));

    // restdocs
    resultActions.andDo(
        document(
            "유저 탈퇴 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(
                fieldWithPath("code").description("응답 코드"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data.userId").description("유저 ID"),
                fieldWithPath("data.socialId").description("소셜 ID"),
                fieldWithPath("data.email").description("탈퇴된 유저의 이메일"),
                fieldWithPath("data.name").description("탈퇴된 유저 이름"),
                fieldWithPath("data.nickname").description("탈퇴된 유저 닉네임"),
                fieldWithPath("data.s3ProfileImageUrl").description("프로필 이미지 URL (탈퇴 후 null)"),
                fieldWithPath("data.leave").description("탈퇴 여부"),
                fieldWithPath("data.gender").description("성별 (탈퇴 후 null)"),
                fieldWithPath("data.birthdate").description("생년월일 (탈퇴 후 null)"))));
  }
}
