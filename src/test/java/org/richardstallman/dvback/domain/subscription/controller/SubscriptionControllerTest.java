package org.richardstallman.dvback.domain.subscription.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.domain.subscription.domain.request.SubscriptionCreateRequestDto;
import org.richardstallman.dvback.domain.subscription.domain.response.SubscriptionResponseDto;
import org.richardstallman.dvback.domain.subscription.service.SubscriptionService;
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
public class SubscriptionControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private SubscriptionService subscriptionService;

  @Test
  @WithMockUser
  @DisplayName("구독 생성 테스트 - 성공")
  void create_subscription() throws Exception {
    // given
    Long userId = 1L;
    Long jobId = 2L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    SubscriptionCreateRequestDto requestDto = new SubscriptionCreateRequestDto(jobId);
    SubscriptionResponseDto responseDto =
        new SubscriptionResponseDto(1L, userId, jobId, LocalDateTime.now());

    when(subscriptionService.createSubscription(
            any(SubscriptionCreateRequestDto.class), eq(userId)))
        .thenReturn(responseDto);

    String content = objectMapper.writeValueAsString(requestDto);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.subscriptionId").value(1L))
        .andExpect(jsonPath("data.userId").value(userId))
        .andExpect(jsonPath("data.jobId").value(jobId));

    // restdocs
    resultActions.andDo(
        document(
            "구독 생성 테스트 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Subscription API")
                    .summary("구독 생성 API")
                    .requestFields(
                        fieldWithPath("jobId").type(JsonFieldType.NUMBER).description("직무 ID"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.subscriptionId")
                            .type(JsonFieldType.NUMBER)
                            .description("구독 ID"),
                        fieldWithPath("data.userId")
                            .type(JsonFieldType.NUMBER)
                            .description("사용자 ID"),
                        fieldWithPath("data.jobId").type(JsonFieldType.NUMBER).description("직무 ID"),
                        fieldWithPath("data.subscribedAt")
                            .type(JsonFieldType.STRING)
                            .description("구독 생성 시각"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("사용자 구독 조회 테스트 - 성공")
  void get_user_subscriptions() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    List<SubscriptionResponseDto> responseDtos =
        List.of(
            new SubscriptionResponseDto(1L, userId, 2L, LocalDateTime.now()),
            new SubscriptionResponseDto(2L, userId, 3L, LocalDateTime.now()));

    when(subscriptionService.getSubscriptionsByUserId(eq(userId))).thenReturn(responseDtos);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/subscription/user")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data[0].subscriptionId").value(1L))
        .andExpect(jsonPath("data[0].jobId").value(2L));

    // restdocs
    resultActions.andDo(
        document(
            "사용자 구독 조회 테스트 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Subscription API")
                    .summary("사용자 구독 조회 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data[].subscriptionId")
                            .type(JsonFieldType.NUMBER)
                            .description("구독 ID"),
                        fieldWithPath("data[].userId")
                            .type(JsonFieldType.NUMBER)
                            .description("사용자 ID"),
                        fieldWithPath("data[].jobId")
                            .type(JsonFieldType.NUMBER)
                            .description("직무 ID"),
                        fieldWithPath("data[].subscribedAt")
                            .type(JsonFieldType.STRING)
                            .description("구독 생성 시각"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("구독 삭제 테스트 - 성공")
  void delete_subscription() throws Exception {
    // given
    Long userId = 1L;
    Long subscriptionId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    doNothing().when(subscriptionService).deleteSubscription(subscriptionId, userId);

    // when
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete(
                    "/subscription/{subscriptionId}", subscriptionId)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isNoContent());

    // restdocs
    resultActions.andDo(
        document(
            "구독 삭제 테스트 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Subscription API")
                    .summary("구독 삭제 API")
                    .description("사용자가 특정 구독을 삭제합니다.")
                    .pathParameters(parameterWithName("subscriptionId").description("삭제할 구독 ID"))
                    .build())));
  }
}
