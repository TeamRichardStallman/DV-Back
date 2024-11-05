package org.richardstallman.dvback.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
import org.richardstallman.dvback.global.jwt.refreshtoken.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

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
    MockCookie authCookie =
        new MockCookie(
            "access_token",
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhY2Nlc3NfdG9rZW4iLCJ1c2VySWQiOjEsImV4cCI6MTczMDc3MjA1NH0.TRlXoLTrhGiKHqvwrZByJB8gpQn9oGU8NZsOoQ1jft16-3giHYytUfHtCJ6-69epvQuhxT4M6Fv8fnsmWZfQiA");

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
    MockCookie accessTokenCookie =
        new MockCookie(
            "access_token",
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhY2Nlc3NfdG9rZW4iLCJ1c2VySWQiOjEsImV4cCI6MTczMDc3MjA1NH0.TRlXoLTrhGiKHqvwrZByJB8gpQn9oGU8NZsOoQ1jft16-3giHYytUfHtCJ6-69epvQuhxT4M6Fv8fnsmWZfQiA");
    MockCookie refreshTokenCookie =
        new MockCookie(
            "refresh_token",
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyZWZyZXNoX3Rva2VuIiwidXNlcklkIjoxLCJleHAiOjE3MzEzNzMyNTR9.fv7nUtmYukFaiBjveoOvSMbo5bpVJ7YFKZr8fDQ-pk1PaZedFBBHQ11WKbVpbwButHrrW5GPJJqLF9E4M5sysg");

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
}