package org.richardstallman.dvback.domain.file.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;
import org.richardstallman.dvback.domain.file.service.CoverLetterService;
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

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class FileControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtUtil jwtUtil;

  @MockBean private S3Service s3Service;
  @MockBean private CoverLetterService coverLetterService;

  @Test
  @WithMockUser
  public void testMyEndpoint() throws Exception {
    mockMvc.perform(get("/ping-pong")).andExpect(status().isOk()).andDo(document("ping-pong"));
  }

  @Test
  @DisplayName("마이페이지에서 자소서 저장")
  @WithMockUser
  void create_cover_letter_on_my_page() throws Exception {
    // given
    Long userId = 1L;
    FileType type = FileType.COVER_LETTER;
    String filePath = "filePath";
    Long fileId = 2L;
    String fileName = "fileName";
    String s3FileUrl = "s3FileUrl";

    CoverLetterRequestDto coverLetterRequestDto = new CoverLetterRequestDto(type, filePath);

    CoverLetterResponseDto coverLetterResponseDto =
        new CoverLetterResponseDto(fileId, fileName, s3FileUrl);

    when(coverLetterService.createCoverLetter(any(CoverLetterRequestDto.class), eq(userId)))
        .thenReturn(coverLetterResponseDto);

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);
    String content = objectMapper.writeValueAsString(coverLetterRequestDto);

    ResultActions resultActions =
        mockMvc.perform(
            post("/file/cover-letter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .cookie(authCookie));

    // restdocs
    resultActions.andDo(
        document(
            "마이페이지에서 자소서 저장",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("마이페이지에서 자소서 저장")
                    .requestFields(
                        fieldWithPath("type")
                            .type(JsonFieldType.STRING)
                            .description("자소서 타입: COVER_LETTER"),
                        fieldWithPath("filePath")
                            .type(JsonFieldType.STRING)
                            .description("s3 파일 경로"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.fileId")
                            .type(JsonFieldType.NUMBER)
                            .description("파일 식별자"),
                        fieldWithPath("data.type").type(JsonFieldType.STRING).description("파일 타입"),
                        fieldWithPath("data.fileName")
                            .type(JsonFieldType.STRING)
                            .description("파일 이름"),
                        fieldWithPath("data.s3FileUrl")
                            .type(JsonFieldType.STRING)
                            .description("파일 경로"))
                    .build())));
  }

  @Test
  @DisplayName("게시글 이미지 업로드 presigned url 생성 - 성공")
  @WithMockUser
  void get_post_image_upload_presigned_url() throws Exception {
    // Given
    Long userId = 1L;
    Long postId = 2L;
    String fileName = "testFile.txt";
    String expectedUrl = "https://s3.aws.com/testFile.txt";
    PreSignedUrlResponseDto preSignedUrlResponseDto =
        new PreSignedUrlResponseDto(expectedUrl, fileName);

    String accessToken = jwtUtil.generateAccessToken(1L);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(s3Service.createPreSignedUrlForPostImage(eq(fileName), eq(postId)))
        .thenReturn(preSignedUrlResponseDto);

    // When & Then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/post-image/{fileName}/{postId}/upload-url", fileName, postId)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // Validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    // REST Docs
    resultActions.andDo(
        document(
            "게시글 이미지 업로드 presigned url 생성",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("게시글 이미지 업로드 presigned url 생성")
                    .pathParameters(
                        parameterWithName("postId").description("게시글 ID"),
                        parameterWithName("fileName").description("업로드할 파일명"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 preSigned Url"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }

  @Test
  @DisplayName("면접 정보 입력 시 PreSigned URL 생성")
  @WithMockUser
  void testGetCoverLetterUploadUrlWhenInputInterviewInfo() throws Exception {
    // Given
    Long userId = 1L;
    Long interviewId = 2L;
    String fileName = "testFile.txt";
    String expectedUrl = "https://s3.aws.com/testFile.txt";
    PreSignedUrlResponseDto preSignedUrlResponseDto =
        new PreSignedUrlResponseDto(expectedUrl, fileName);

    String accessToken = jwtUtil.generateAccessToken(1L);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(s3Service.createPreSignedURLForInterview(
            eq(FileType.COVER_LETTER), eq(fileName), eq(userId), eq(interviewId), isNull()))
        .thenReturn(preSignedUrlResponseDto);

    // When & Then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter/{interviewId}/{fileName}/upload-url", interviewId, fileName)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // Validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("data.preSignedUrl").value(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "면접 정보 입력 시 PreSigned URL 생성",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("파일 API")
                    .pathParameters(
                        parameterWithName("interviewId").description("면접 ID"),
                        parameterWithName("fileName").description("업로드할 파일명"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 preSigned Url"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }

  @Test
  @DisplayName("면접 자소서 다운로드 url 받기 - 성공")
  @WithMockUser
  void testGetInterviewCoverLetterDownloadUrl() throws Exception {
    // given
    Long userId = 1L;
    Long interviewId = 2L;
    String expectedUrl = "https://s3.aws.com/cover-letter/1/cover-letter-test.txt";
    String path = "/files/cover-letter/2/1/cover-letter-test.txt";
    PreSignedUrlResponseDto preSignedUrlResponseDto =
        new PreSignedUrlResponseDto(expectedUrl, path);
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(coverLetterService.findCoverLetterByInterviewId(interviewId))
        .thenReturn(
            CoverLetterDomain.builder()
                .s3FileUrl("/files/cover-letter/2/1/cover-letter-test.txt")
                .build());
    when(s3Service.getDownloadURLForInterview(eq(path), eq(userId), eq(interviewId)))
        .thenReturn(preSignedUrlResponseDto);

    // when & then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter/{interviewId}/download-url", interviewId)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.preSignedUrl").value(expectedUrl));

    // rest docs
    resultActions.andDo(
        document(
            "면접 자소서 다운로드 url 받기 - 성공",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("파일 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 preSigned Url"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }

  @Test
  @DisplayName("유저 자소서 다운로드 url 받기 - 성공")
  @WithMockUser
  void testGetUserCoverLetterDownloadUrl() throws Exception {
    // given
    Long userId = 1L;
    Long coverLetterId = 100L;
    String expectedUrl = "https://s3.aws.com/9e715748-52d8-49b2-96dd-a436e9b15903.docx";
    String path = "9e715748-52d8-49b2-96dd-a436e9b15903.docx";
    PreSignedUrlResponseDto preSignedUrlResponseDto =
        new PreSignedUrlResponseDto(expectedUrl, path);
    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    when(coverLetterService.findCoverLetter(coverLetterId))
        .thenReturn(
            CoverLetterDomain.builder()
                .s3FileUrl("9e715748-52d8-49b2-96dd-a436e9b15903.docx")
                .build());
    when(s3Service.getDownloadURL(eq(path), eq(userId))).thenReturn(preSignedUrlResponseDto);

    // when & then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter/user/{coverLetterId}/download-url", coverLetterId)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    // rest docs
    resultActions.andDo(
        document(
            "유저 자소서 다운로드 url 받기 - 성공",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("파일 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 preSigned Url"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }

  @Test
  @DisplayName("마이페이지에서 PreSigned URL 생성")
  @WithMockUser
  void testGetCoverLetterUploadUrlOnMyPage() throws Exception {
    // Given
    Long userId = 1L;
    String fileName = "testFile.txt";
    String expectedUrl = "https://s3.aws.com/testFile.txt";
    PreSignedUrlResponseDto preSignedUrlResponseDto =
        new PreSignedUrlResponseDto(expectedUrl, fileName);

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(s3Service.createPreSignedURLForInterview(
            eq(FileType.COVER_LETTER), eq(fileName), eq(userId), isNull(), isNull()))
        .thenReturn(preSignedUrlResponseDto);

    // When & Then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter/{fileName}/upload-url", fileName)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // Validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("data.preSignedUrl").value(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "마이페이지에서 PreSigned URL 생성",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("파일 API")
                    .pathParameters(parameterWithName("fileName").description("업로드할 파일명"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 PreSigned Url"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }

  @Test
  @DisplayName("프로필 이미지 PreSignedUrl 받기 - 성공")
  @WithMockUser
  void testGetProfileImageUploadUrl() throws Exception {
    // given
    Long userId = 1L;
    String fileName = "profile-img.jpg";
    String expectedUrl = "https://s3.aws.com/profile-image/1/profile-image.jpg";

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // mock s3Service
    when(s3Service.getPreSignedUrlForImage(eq(fileName), eq(userId)))
        .thenReturn(new PreSignedUrlResponseDto(expectedUrl, fileName));

    // when & then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/profile-image/{fileName}/upload-url", fileName)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("data.preSignedUrl").value(expectedUrl));

    // rest docs
    resultActions.andDo(
        document(
            "프로필 이미지 PreSignedUrl 받기 - 성공",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("파일 API")
                    .pathParameters(parameterWithName("fileName").description("업로드할 파일명"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 preSigned Url"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }

  @Test
  @DisplayName("유저가 올린 자소서 목록 조회")
  @WithMockUser
  void testGetCoverLetterByUserId() throws Exception {
    // Given
    Long userId = 1L;
    List<CoverLetterResponseDto> expectedCoverLetterResponseDtos = new ArrayList<>();
    expectedCoverLetterResponseDtos.add(
        new CoverLetterResponseDto(1L, "testFile.txt", "https://s3.aws.com/testFile.txt"));
    expectedCoverLetterResponseDtos.add(
        new CoverLetterResponseDto(2L, "testFile2.txt", "https://s3.aws.com/testFile2.txt"));
    expectedCoverLetterResponseDtos.add(
        new CoverLetterResponseDto(3L, "testFile3.txt", "https://s3.aws.com/testFile3.txt"));

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    // Mock S3Service behavior
    when(coverLetterService.findCoverLettersByUserId(userId))
        .thenReturn(expectedCoverLetterResponseDtos);

    // When & Then
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/cover-letter").cookie(authCookie).accept(MediaType.APPLICATION_JSON));

    // Validate the response
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("code").value(200))
        .andExpect(jsonPath("message").value("SUCCESS"))
        .andExpect(jsonPath("data.coverLetters[0].fileId").value(1))
        .andExpect(jsonPath("data.coverLetters[0].type").value("COVER_LETTER"))
        .andExpect(jsonPath("data.coverLetters[0].fileName").value("testFile.txt"))
        .andExpect(
            jsonPath("data.coverLetters[0].s3FileUrl").value("https://s3.aws.com/testFile.txt"))
        .andExpect(jsonPath("data.coverLetters[1].fileId").value(2))
        .andExpect(jsonPath("data.coverLetters[1].type").value("COVER_LETTER"))
        .andExpect(jsonPath("data.coverLetters[1].fileName").value("testFile2.txt"))
        .andExpect(
            jsonPath("data.coverLetters[1].s3FileUrl").value("https://s3.aws.com/testFile2.txt"))
        .andExpect(jsonPath("data.coverLetters[2].fileId").value(3))
        .andExpect(jsonPath("data.coverLetters[2].type").value("COVER_LETTER"))
        .andExpect(jsonPath("data.coverLetters[2].fileName").value("testFile3.txt"))
        .andExpect(
            jsonPath("data.coverLetters[2].s3FileUrl").value("https://s3.aws.com/testFile3.txt"));

    // REST Docs
    resultActions.andDo(
        document(
            "유저가 올린 자소서 목록 조회",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("파일 API")
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.coverLetters[0].fileId")
                            .type(JsonFieldType.NUMBER)
                            .description("자소서 식별자"),
                        fieldWithPath("data.coverLetters[0].fileName")
                            .type(JsonFieldType.STRING)
                            .description("자소서 파일 이름"),
                        fieldWithPath("data.coverLetters[0].s3FileUrl")
                            .type(JsonFieldType.STRING)
                            .description("자소서 업로드 주소"),
                        fieldWithPath("data.coverLetters[0].type")
                            .type(JsonFieldType.STRING)
                            .description("파일 유형: COVER_LETTER"))
                    .build())));
  }

  @Test
  @DisplayName("오디오 파일 업로드를 위한 PreSigned URL 생성 - 성공")
  @WithMockUser
  void testGetAudioUploadUrlSuccess() throws Exception {
    // Given
    Long userId = 1L;
    Long interviewId = 2L;
    Long questionId = 3L;
    String expectedUrl = "https://s3.aws.com/audio/1/interview/2/question/3/audio-test.mp3";

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    PreSignedUrlResponseDto preSignedUrlResponseDto =
        new PreSignedUrlResponseDto(expectedUrl, "audio/1/interview/2/question/3/audio-test.mp3");

    // Mock S3Service behavior
    when(s3Service.createPreSignedURLForAudio(
            eq(FileType.AUDIO_ANSWER.getFolderName()),
            eq(userId),
            eq(interviewId),
            eq(questionId),
            isNull()))
        .thenReturn(preSignedUrlResponseDto);

    // When
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/audio/{interviewId}/{questionId}/upload-url", interviewId, questionId)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // Then
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("data.preSignedUrl").value(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "오디오 파일 업로드 PreSigned URL 생성",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("오디오 파일 업로드를 위한 PreSigned URL 생성")
                    .pathParameters(
                        parameterWithName("interviewId").description("면접 ID"),
                        parameterWithName("questionId").description("질문 ID"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("AWS S3 PreSigned URL"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }

  @Test
  @DisplayName("오디오 파일 다운로드를 위한 PreSigned URL 생성 - 성공")
  @WithMockUser
  void testGetAudioDownloadUrlSuccess() throws Exception {
    // Given
    Long userId = 1L;
    Long interviewId = 2L;
    Long questionId = 3L;
    String s3FileUrl = "audio/1/interview/2/question/3/audio-test.mp3";
    String expectedUrl = "https://s3.aws.com/" + s3FileUrl;

    String accessToken = jwtUtil.generateAccessToken(userId);
    MockCookie authCookie = new MockCookie("access_token", accessToken);

    PreSignedUrlResponseDto preSignedUrlResponseDto =
        new PreSignedUrlResponseDto(expectedUrl, s3FileUrl);

    // Mock CoverLetterService behavior
    when(coverLetterService.findCoverLetterByInterviewId(interviewId))
        .thenReturn(CoverLetterDomain.builder().s3FileUrl(s3FileUrl).build());

    // Mock S3Service behavior
    when(s3Service.getDownloadURLForAudio(
            eq(s3FileUrl), eq(userId), eq(interviewId), eq(questionId)))
        .thenReturn(preSignedUrlResponseDto);

    // When
    ResultActions resultActions =
        mockMvc.perform(
            get("/file/audio/{interviewId}/{questionId}/download-url", interviewId, questionId)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON));

    // Then
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("data.preSignedUrl").value(expectedUrl));

    // REST Docs
    resultActions.andDo(
        document(
            "오디오 파일 다운로드 PreSigned URL 생성 - 성공",
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("File API")
                    .summary("오디오 파일 다운로드를 위한 PreSigned URL 생성")
                    .pathParameters(
                        parameterWithName("interviewId").description("면접 ID"),
                        parameterWithName("questionId").description("질문 ID"))
                    .responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.preSignedUrl")
                            .type(JsonFieldType.STRING)
                            .description("AWS S3 PreSigned URL"),
                        fieldWithPath("data.objectKey")
                            .type(JsonFieldType.STRING)
                            .description("aws s3 object key"))
                    .build())));
  }
}
