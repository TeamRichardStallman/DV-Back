package org.richardstallman.dvback.domain.point.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.domain.point.domain.response.PointResponseDto;
import org.richardstallman.dvback.domain.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class PointControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PointService pointService;

  @Test
  @WithMockUser
  @DisplayName("포인트 조회 테스트")
  void getPointByUserIdTest() throws Exception {
    // given
    Long userId = 1L;
    when(pointService.getPointByUserId(userId)).thenReturn(new PointResponseDto(1000));

    // when
    ResultActions result =
        mockMvc.perform(
            get("/point/user/{userId}", userId)
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("data.balance").value(1000));

    // restdocs
    result.andDo(
        document(
            "포인트 조회 테스트",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            pathParameters(parameterWithName("userId").description("유저 식별자")),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data.balance").type(JsonFieldType.NUMBER).description("포인트 잔액"))));
  }
}
