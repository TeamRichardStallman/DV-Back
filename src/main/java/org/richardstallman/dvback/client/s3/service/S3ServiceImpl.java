package org.richardstallman.dvback.client.s3.service;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;
import org.richardstallman.dvback.domain.file.domain.response.PreSignedUrlResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  @Autowired private final S3Presigner s3Presigner;

  @Autowired private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Value("${cloud.aws.s3.baseFilePath}")
  private String baseFilePath;

  @Value("${cloud.aws.s3.urlDurationMinutes}")
  private long urlDurationMinutes;

  private Duration urlDuration;

  @PostConstruct
  private void init() {
    this.urlDuration = Duration.ofMinutes(urlDurationMinutes);
  }

  @Override
  public PreSignedUrlResponseDto getPreSignedUrlForImage(String fileName, Long userId) {
    String filePathKey = makeS3FilePathForImage(FileType.PROFILE_IMAGE, fileName, userId);

    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3Presigner.presignGetObject(
            builder -> builder.signatureDuration(urlDuration).getObjectRequest(getObjectRequest));

    return new PreSignedUrlResponseDto(presignedGetObjectRequest.url().toString(), filePathKey);
  }

  @Override
  public PreSignedUrlResponseDto createPreSignedUrlForPostImage(String fileName, Long postId) {
    String filePathKey = makeS3FilePathForImage(FileType.POST_IMAGE, fileName, postId);

    PutObjectRequest getObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedPutObjectRequest presignedPutObjectRequest =
        s3Presigner.presignPutObject(
            builder -> builder.signatureDuration(urlDuration).putObjectRequest(getObjectRequest));
    return new PreSignedUrlResponseDto(presignedPutObjectRequest.url().toString(), filePathKey);
  }

  @Override
  public PreSignedUrlResponseDto createPreSignedURLForInterview(
      FileType fileType,
      String fileName,
      Long userId,
      @Nullable Long interviewId,
      @Nullable Map<String, String> metadata) {
    String filePathKey = makeS3FilePathForInterview(fileType, fileName, userId, interviewId);

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedPutObjectRequest presignedPutObjectRequest =
        s3Presigner.presignPutObject(
            builder -> builder.signatureDuration(urlDuration).putObjectRequest(putObjectRequest));

    return new PreSignedUrlResponseDto(presignedPutObjectRequest.url().toString(), filePathKey);
  }

  @Override
  public PreSignedUrlResponseDto getDownloadURLForInterview(
      String filePath, Long userId, @Nullable Long interviewId) {

    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(filePath).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3Presigner.presignGetObject(
            builder -> builder.signatureDuration(urlDuration).getObjectRequest(getObjectRequest));

    return new PreSignedUrlResponseDto(presignedGetObjectRequest.url().toString(), filePath);
  }

  @Override
  public PreSignedUrlResponseDto getDownloadURL(String filePath, Long userId) {

    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(filePath).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3Presigner.presignGetObject(
            builder -> builder.signatureDuration(urlDuration).getObjectRequest(getObjectRequest));

    return new PreSignedUrlResponseDto(presignedGetObjectRequest.url().toString(), filePath);
  }

  @Override
  public PreSignedUrlResponseDto createPreSignedURLForAudio(
      String fileType,
      Long userId,
      @Nullable Long interviewId,
      @Nullable Long questionId,
      @Nullable Map<String, String> metadata) {
    String filePathKey = makeS3FilePathForAudio(fileType, userId, interviewId, questionId);

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(filePathKey).build();

    PresignedPutObjectRequest presignedPutObjectRequest =
        s3Presigner.presignPutObject(
            builder -> builder.signatureDuration(urlDuration).putObjectRequest(putObjectRequest));

    return new PreSignedUrlResponseDto(presignedPutObjectRequest.url().toString(), filePathKey);
  }

  @Override
  public PreSignedUrlResponseDto getDownloadURLForAudio(
      String filePath, Long userId, @Nullable Long interviewId, @Nullable Long questionId) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(filePath).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3Presigner.presignGetObject(
            builder -> builder.signatureDuration(urlDuration).getObjectRequest(getObjectRequest));

    return new PreSignedUrlResponseDto(presignedGetObjectRequest.url().toString(), filePath);
  }

  private String makeS3FilePathForImage(FileType fileType, String fileName, Long userId) {
    return String.format("%s/%d/%s", fileType.getFolderName(), userId, fileName);
  }

  private String makeS3FilePathForInterview(
      FileType fileType, String fileName, Long userId, @Nullable Long interviewId) {
    String timestamp = String.valueOf(System.currentTimeMillis());

    if (interviewId != null) {
      // 면접 정보 입력 시 업로드 경로
      return String.format(
          "users/%d/interviews/%d/docs/%s/%d-%s-%s",
          userId, interviewId, fileType.getFolderName(), userId, timestamp, fileName);
    } else {
      // 미리 업로드 경로
      return String.format(
          "users/%d/docs/%s/%d-%s-%s",
          userId, fileType.getFolderName(), userId, timestamp, fileName);
    }
  }

  private String makeS3FilePathForAudio(
      String fileType, Long userId, @Nullable Long interviewId, @Nullable Long questionId) {
    String timestamp = String.valueOf(System.currentTimeMillis());
    return String.format(
        "users/%d/interviews/%d/questions/%d/%d-%d-%d-%s-%s.mp3",
        userId, interviewId, questionId, userId, interviewId, questionId, timestamp, fileType);
  }
}
