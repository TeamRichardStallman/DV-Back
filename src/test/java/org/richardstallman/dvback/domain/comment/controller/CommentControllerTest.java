package org.richardstallman.dvback.domain.comment.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.richardstallman.dvback.domain.comment.domain.request.CommentCreateRequestDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentDeleteResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentListResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentResponseDto;
import org.richardstallman.dvback.domain.comment.service.CommentService;
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
public class CommentControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired private JwtUtil jwtUtil;

  @MockBean private CommentService commentService;

  @Test
  @WithMockUser
  @DisplayName("댓글 작성 테스트 - 성공")
  void create_comment() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    CommentCreateRequestDto commentCreateRequestDto = new CommentCreateRequestDto(1L, "댓글 내용입니다.");
    CommentResponseDto commentResponseDto =
        new CommentResponseDto(1L, 1L, "댓글 내용입니다.", LocalDateTime.now());

    when(commentService.createComment(any(CommentCreateRequestDto.class), eq(userId)))
        .thenReturn(commentResponseDto);

    String content = objectMapper.writeValueAsString(commentCreateRequestDto);
    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.commentId").value(1));

    // restdocs
    resultActions.andDo(
        document(
            "댓글 작성 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Comment API")
                    .summary("댓글 API")
                    .requestFields(
                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                        fieldWithPath("comment").type(JsonFieldType.STRING).description("댓글 내용"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.commentId")
                            .type(JsonFieldType.NUMBER)
                            .description("댓글 식별자"),
                        fieldWithPath("data.postId")
                            .type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.comment")
                            .type(JsonFieldType.STRING)
                            .description("댓글 내용"),
                        fieldWithPath("data.generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("댓글 작성 일시"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("댓글 삭제 테스트 - 성공")
  void delete_comment() throws Exception {
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    Long commentId = 1L;
    CommentDeleteResponseDto commentDeleteResponseDto =
        new CommentDeleteResponseDto(1, "댓글이 삭제되었습니다.");

    when(commentService.deleteComment(eq(commentId))).thenReturn(commentDeleteResponseDto);

    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/comment/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.count").value(1));

    // restdocs
    resultActions.andDo(
        document(
            "댓글 삭제 - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Comment API")
                    .summary("댓글 API")
                    .pathParameters(parameterWithName("commentId").description("댓글 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.count")
                            .type(JsonFieldType.NUMBER)
                            .description("삭제된 댓글 개수"),
                        fieldWithPath("data.message")
                            .type(JsonFieldType.STRING)
                            .description("댓글 삭제 안내문(\"댓글이 삭제되었습니다.\""))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("댓글 목록 조회(게시글 작성순) 테스트 - 성공")
  void get_comment_list_by_post() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    Long postId = 1L;
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    CommentResponseDto commentResponseDto1 =
        new CommentResponseDto(1L, postId, "댓글1", LocalDateTime.now());
    CommentResponseDto commentResponseDto2 =
        new CommentResponseDto(2L, postId, "댓글2", LocalDateTime.now());
    CommentResponseDto commentResponseDto3 =
        new CommentResponseDto(3L, postId, "댓글3", LocalDateTime.now());
    List<CommentResponseDto> commentResponseDtos =
        List.of(commentResponseDto1, commentResponseDto2, commentResponseDto3);

    when(commentService.getCommentListByPostId(eq(postId)))
        .thenReturn(new CommentListResponseDto(commentResponseDtos));

    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/comment/post/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.comments[0].postId").value(postId));

    // restdocs
    resultActions.andDo(
        document(
            "댓글 목록 조회(게시글 작성순) - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Comment API")
                    .summary("댓글 API")
                    .pathParameters(parameterWithName("postId").description("게시글 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.comments[].commentId")
                            .type(JsonFieldType.NUMBER)
                            .description("댓글 식별자"),
                        fieldWithPath("data.comments[].postId")
                            .type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.comments[].comment")
                            .type(JsonFieldType.STRING)
                            .description("댓글 내용"),
                        fieldWithPath("data.comments[].generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("댓글 작성 일시"))
                    .build())));
  }

  @Test
  @WithMockUser
  @DisplayName("댓글 목록 조회(유저가 작성한 댓글 목록 최신순) 테스트 - 성공")
  void get_comment_list_by_comment_author() throws Exception {
    // given
    Long userId = 1L;
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    CommentResponseDto commentResponseDto1 =
        new CommentResponseDto(1L, 1L, "댓글1", LocalDateTime.now());
    CommentResponseDto commentResponseDto2 =
        new CommentResponseDto(2L, 3L, "댓글2", LocalDateTime.now());
    CommentResponseDto commentResponseDto3 =
        new CommentResponseDto(3L, 8L, "댓글3", LocalDateTime.now());
    List<CommentResponseDto> commentResponseDtos =
        List.of(commentResponseDto1, commentResponseDto2, commentResponseDto3);

    when(commentService.getCommentListByCommentAuthorId(eq(userId)))
        .thenReturn(new CommentListResponseDto(commentResponseDtos));

    ResultActions resultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/comment/author/{commentAuthorId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(authCookie));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.comments[0].postId").value(1L));

    // restdocs
    resultActions.andDo(
        document(
            "댓글 목록 조회(유저가 작성한 댓글 목록 최신순) - 성공",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("Comment API")
                    .summary("댓글 API")
                    .pathParameters(parameterWithName("commentAuthorId").description("댓글 작성자 식별자"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.comments[].commentId")
                            .type(JsonFieldType.NUMBER)
                            .description("댓글 식별자"),
                        fieldWithPath("data.comments[].postId")
                            .type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.comments[].comment")
                            .type(JsonFieldType.STRING)
                            .description("댓글 내용"),
                        fieldWithPath("data.comments[].generatedAt")
                            .type(JsonFieldType.STRING)
                            .description("댓글 작성 일시"))
                    .build())));
  }
}
