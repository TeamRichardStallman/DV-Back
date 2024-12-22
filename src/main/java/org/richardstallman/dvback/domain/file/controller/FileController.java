package org.richardstallman.dvback.domain.file.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterListResponseDto;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;
import org.richardstallman.dvback.domain.file.service.CoverLetterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/file", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

  private final S3Service s3Service;
  private final CoverLetterService coverLetterService;

  @PostMapping("/cover-letter")
  public ResponseEntity<DvApiResponse<CoverLetterResponseDto>> createCoverLetter(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody final CoverLetterRequestDto coverLetterRequestDto) {
    log.info("");
    final CoverLetterResponseDto coverLetterResponseDto =
        coverLetterService.createCoverLetter(coverLetterRequestDto, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(DvApiResponse.of(coverLetterResponseDto));
  }

  @GetMapping("/post-image/{fileName}/{postId}/upload-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getPostImageUploadUrl(
      @PathVariable String fileName, @PathVariable Long postId) {
    log.info("Generating preSigned URL for file upload: fileName={}", fileName);

    PreSignedUrlResponseDto preSignedUrl =
        s3Service.createPreSignedUrlForPostImage(fileName, postId);
    log.info("PreSigned URL: {}", preSignedUrl);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/cover-letter/{interviewId}/{fileName}/upload-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>>
      getCoverLetterUploadUrlWhenInputInterviewInfo(
          @AuthenticationPrincipal Long userId,
          @PathVariable Long interviewId,
          @PathVariable String fileName) {
    log.info(
        "Generating preSigned URL for file upload: interviewId={}, fileName={}",
        interviewId,
        fileName);

    PreSignedUrlResponseDto preSignedUrl =
        s3Service.createPreSignedURLForInterview(
            FileType.COVER_LETTER, fileName, userId, interviewId, null);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/cover-letter/{fileName}/upload-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getCoverLetterUploadUrlOnMyPage(
      @AuthenticationPrincipal Long userId, @PathVariable String fileName) {
    log.info("Generating preSigned URL for file upload: fileName={}", fileName);

    PreSignedUrlResponseDto preSignedUrl =
        s3Service.createPreSignedURLForInterview(
            FileType.COVER_LETTER, fileName, userId, null, null);

    log.info("Generated preSigned URL on my page: {}", preSignedUrl);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/cover-letter/{interviewId}/download-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getCoverLetterDownloadUrlOnMyPage(
      @AuthenticationPrincipal Long userId, @PathVariable Long interviewId) {
    log.info("Generating preSigned URL for file download: interviewId={}", interviewId);

    PreSignedUrlResponseDto preSignedUrlResponseDto =
        s3Service.getDownloadURLForInterview(
            coverLetterService.findCoverLetterByInterviewId(interviewId).getS3FileUrl(),
            userId,
            interviewId);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrlResponseDto));
  }

  @GetMapping("/cover-letter/user/{coverLetterId}/download-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>>
      getCoverLetterDownloadUrlOnFileSelect(
          @AuthenticationPrincipal Long userId, @PathVariable Long coverLetterId) {
    log.info(
        "Generating preSigned URL for file download on File Select: coverLetterId={}",
        coverLetterId);

    PreSignedUrlResponseDto preSignedUrlResponseDto =
        s3Service.getDownloadURL(
            coverLetterService.findCoverLetter(coverLetterId).getS3FileUrl(), userId);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrlResponseDto));
  }

  @GetMapping("/profile-image/{fileName}/upload-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getProfileImageUploadUrl(
      @AuthenticationPrincipal Long userId, @PathVariable String fileName) {
    log.info("Generating preSigned URL for profile image upload: userId={}", userId);

    PreSignedUrlResponseDto preSignedUrl = s3Service.getPreSignedUrlForImage(fileName, userId);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/cover-letter")
  public ResponseEntity<DvApiResponse<CoverLetterListResponseDto>> getUserCoverLetterList(
      @AuthenticationPrincipal Long userId) {
    log.info("Get Cover Letter List for User=({})", userId);

    CoverLetterListResponseDto coverLetterListResponseDto =
        new CoverLetterListResponseDto(coverLetterService.findCoverLettersByUserId(userId));
    return ResponseEntity.ok(DvApiResponse.of(coverLetterListResponseDto));
  }

  @GetMapping("/audio/{interviewId}/{questionId}/upload-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getAudioUploadUrl(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long interviewId,
      @PathVariable Long questionId) {
    log.info(
        "Generating preSigned URL for file upload: interviewId={}, questionId={}",
        interviewId,
        questionId);
    PreSignedUrlResponseDto preSignedUrl =
        s3Service.createPreSignedURLForAudio(
            FileType.AUDIO_ANSWER.getFolderName(), userId, interviewId, questionId, null);
    log.info("Generated preSigned URL: {}", preSignedUrl);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(DvApiResponse.of(preSignedUrl));
  }

  @GetMapping("/audio/{interviewId}/{questionId}/download-url")
  public ResponseEntity<DvApiResponse<PreSignedUrlResponseDto>> getAudioDownloadUrl(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long interviewId,
      @PathVariable Long questionId) {
    log.info(
        "Generating preSigned URL for file download: interviewId={}, questionId={}",
        interviewId,
        questionId);

    PreSignedUrlResponseDto preSignedUrlResponseDto =
        s3Service.getDownloadURLForAudio(
            coverLetterService.findCoverLetterByInterviewId(interviewId).getS3FileUrl(),
            userId,
            interviewId,
            questionId);

    return ResponseEntity.ok(DvApiResponse.of(preSignedUrlResponseDto));
  }
}
