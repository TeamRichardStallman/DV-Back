package org.richardstallman.dvback.client.s3.service;

import jakarta.annotation.Nullable;
import java.util.Map;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;

public interface S3Service {

  PreSignedUrlResponseDto getPreSignedUrlForImage(String fileName, Long userId);

  PreSignedUrlResponseDto createPreSignedUrlForPostImage(String fileName, Long postId);

  PreSignedUrlResponseDto createPreSignedURLForInterview(
      FileType fileType,
      String fileName,
      Long userId,
      @Nullable Long interviewId,
      @Nullable Map<String, String> metadata);

  PreSignedUrlResponseDto getDownloadURLForInterview(
      String filePath, Long userId, @Nullable Long interviewId);

  PreSignedUrlResponseDto getDownloadURL(String filePath, Long userId);

  PreSignedUrlResponseDto createPreSignedURLForAudio(
      String fileType,
      Long userId,
      @Nullable Long interviewId,
      @Nullable Long questionId,
      @Nullable Map<String, String> metadata);

  PreSignedUrlResponseDto getDownloadURLForAudio(
      String filePath, Long userId, @Nullable Long interviewId, @Nullable Long questionId);
}
