package org.richardstallman.dvback.domain.post.converter;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.evaluation.converter.OverallEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.job.converter.JobConverter;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;
import org.richardstallman.dvback.domain.post.entity.PostEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConverter {

  private final UserConverter userConverter;
  private final JobConverter jobConverter;
  private final InterviewConverter interviewConverter;
  private final OverallEvaluationConverter overallEvaluationConverter;

  public PostDomain fromEntityToDomain(PostEntity postEntity) {
    return PostDomain.builder()
        .postId(postEntity.getPostId())
        .authorDomain(userConverter.fromEntityToDomain(postEntity.getAuthor()))
        .jobDomain(jobConverter.toDomain(postEntity.getJob()))
        .content(postEntity.getContent())
        .s3ImageUrl(postEntity.getS3ImageUrl())
        .interviewDomain(
            postEntity.getInterview() == null
                ? null
                : interviewConverter.fromEntityToDomain(postEntity.getInterview()))
        .overallEvaluationDomain(
            postEntity.getOverallEvaluation() == null
                ? null
                : overallEvaluationConverter.fromEntityToDomain(postEntity.getOverallEvaluation()))
        .postType(postEntity.getPostType())
        .generatedAt(postEntity.getGeneratedAt())
        .build();
  }

  public PostEntity fromDomainToEntity(PostDomain postDomain) {
    return new PostEntity(
        postDomain.getPostId(),
        userConverter.fromDomainToEntity(postDomain.getAuthorDomain()),
        jobConverter.toEntity(postDomain.getJobDomain()),
        postDomain.getContent(),
        postDomain.getS3ImageUrl(),
        postDomain.getInterviewDomain() == null
            ? null
            : interviewConverter.fromDomainToEntity(postDomain.getInterviewDomain()),
        postDomain.getOverallEvaluationDomain() == null
            ? null
            : overallEvaluationConverter.fromDomainToEntity(
                postDomain.getOverallEvaluationDomain()),
        postDomain.getPostType(),
        postDomain.getGeneratedAt());
  }

  public PostDomain fromCreateRequestDtoToDomain(
      PostCreateRequestDto postCreateRequestDto,
      UserDomain userDomain,
      JobDomain jobDomain,
      InterviewDomain interviewDomain,
      OverallEvaluationDomain overallEvaluationDomain,
      LocalDateTime generatedAt) {
    return PostDomain.builder()
        .authorDomain(userDomain)
        .jobDomain(jobDomain)
        .content(postCreateRequestDto.content())
        .s3ImageUrl(postCreateRequestDto.s3ImageUrl())
        .interviewDomain(interviewDomain)
        .overallEvaluationDomain(overallEvaluationDomain)
        .postType(postCreateRequestDto.postType())
        .generatedAt(generatedAt)
        .build();
  }

  public PostCreateResponseDto fromDomainToCreateResponseDto(
      PostDomain postDomain,
      InterviewResponseDto interviewResponseDto,
      OverallEvaluationResponseDto overallEvaluationResponseDto,
      String s3ProfileUrl,
      String s3PostImageUrl) {
    return new PostCreateResponseDto(
        postDomain.getPostId(),
        postDomain.getAuthorDomain().getUserId(),
        postDomain.getAuthorDomain().getNickname(),
        s3ProfileUrl,
        postDomain.getJobDomain().getJobName(),
        postDomain.getJobDomain().getJobNameKorean(),
        postDomain.getContent(),
        postDomain.getS3ImageUrl() == null ? null : s3PostImageUrl,
        interviewResponseDto,
        overallEvaluationResponseDto,
        postDomain.getPostType(),
        postDomain.getGeneratedAt());
  }

  public PostDomain addImage(PostDomain postDomain, String imageUrl) {
    return PostDomain.builder()
        .postId(postDomain.getPostId())
        .authorDomain(postDomain.getAuthorDomain())
        .jobDomain(postDomain.getJobDomain())
        .content(postDomain.getContent())
        .s3ImageUrl(imageUrl)
        .interviewDomain(postDomain.getInterviewDomain())
        .overallEvaluationDomain(postDomain.getOverallEvaluationDomain())
        .postType(postDomain.getPostType())
        .generatedAt(postDomain.getGeneratedAt())
        .build();
  }
}
