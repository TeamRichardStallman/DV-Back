package org.richardstallman.dvback.domain.post.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.s3.service.S3Service;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.evaluation.service.EvaluationService;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;
import org.richardstallman.dvback.domain.interview.service.InterviewService;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.domain.post.converter.PostConverter;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;
import org.richardstallman.dvback.domain.post.repository.PostRepository;
import org.richardstallman.dvback.domain.subscription.domain.SubscriptionDomain;
import org.richardstallman.dvback.domain.subscription.repostiroy.SubscriptionRepository;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.richardstallman.dvback.global.util.TimeUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final PostConverter postConverter;
  private final UserRepository userRepository;
  private final JobService jobService;
  private final InterviewService interviewService;
  private final EvaluationService evaluationService;
  private final SubscriptionRepository subscriptionRepository;
  private final S3Service s3Service;

  @Override
  public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId) {
    UserDomain userDomain = getUser(userId);
    JobDomain jobDomain = jobService.findJobByKoreanName(postCreateRequestDto.jobKoreanName());
    InterviewDomain interviewDomain =
        postCreateRequestDto.interviewId() == null
            ? null
            : interviewService.getInterviewById(postCreateRequestDto.interviewId());
    OverallEvaluationDomain overallEvaluationDomain =
        postCreateRequestDto.overallEvaluationId() == null
            ? null
            : evaluationService.getOverallEvaluationDomainById(
                postCreateRequestDto.overallEvaluationId());
    LocalDateTime generatedAt = TimeUtil.getCurrentDateTime();
    PostDomain postDomain =
        postRepository.save(
            postConverter.fromCreateRequestDtoToDomain(
                postCreateRequestDto,
                userDomain,
                jobDomain,
                interviewDomain,
                overallEvaluationDomain,
                generatedAt));
    InterviewResponseDto interviewResponseDto = getInterviewResponseDtoByDomain(interviewDomain);
    OverallEvaluationResponseDto overallEvaluationResponseDto =
        getOverallEvaluationResponseDtoByDomain(overallEvaluationDomain);

    String s3ProfileImageUrl =
        userDomain.getS3ProfileImageObjectKey() == null
            ? null
            : getProfileImagePreSignedUrl(userDomain);

    return postConverter.fromDomainToCreateResponseDto(
        postDomain, interviewResponseDto, overallEvaluationResponseDto, s3ProfileImageUrl, null);
  }

  @Override
  public PostCreateResponseDto addImage(String imageUrl, Long postId) {
    PostDomain postDomain = getPost(postId);
    PostDomain updatedPostDomain =
        postRepository.save(postConverter.addImage(postDomain, imageUrl));
    InterviewResponseDto interviewResponseDto =
        getInterviewResponseDtoByDomain(updatedPostDomain.getInterviewDomain());
    OverallEvaluationResponseDto overallEvaluationResponseDto =
        getOverallEvaluationResponseDtoByDomain(updatedPostDomain.getOverallEvaluationDomain());
    String s3ProfileImageUrl =
        updatedPostDomain.getAuthorDomain().getS3ProfileImageObjectKey() == null
            ? null
            : getProfileImagePreSignedUrl(updatedPostDomain.getAuthorDomain());
    return postConverter.fromDomainToCreateResponseDto(
        updatedPostDomain,
        interviewResponseDto,
        overallEvaluationResponseDto,
        s3ProfileImageUrl,
        getPostImagePreSignedUrl(
            imageUrl, updatedPostDomain.getAuthorDomain().getUserId(), postId));
  }

  @Override
  public List<PostCreateResponseDto> getPostsByUserId(Long userId) {
    List<PostDomain> postDomains = postRepository.findByAuthorId(userId);
    log.info("getPostsByUserId::postDomains length: {}", postDomains.size());

    return postDomains.stream()
        .map(
            (e) ->
                postConverter.fromDomainToCreateResponseDto(
                    e,
                    getInterviewResponseDtoByDomain(e.getInterviewDomain()),
                    getOverallEvaluationResponseDtoByDomain(e.getOverallEvaluationDomain()),
                    e.getAuthorDomain().getS3ProfileImageObjectKey() == null
                        ? null
                        : getProfileImagePreSignedUrl(e.getAuthorDomain()),
                    e.getS3ImageUrl() == null || e.getS3ImageUrl().isEmpty()
                        ? null
                        : getPostImagePreSignedUrl(e.getS3ImageUrl(), userId, e.getPostId())))
        .toList();
  }

  private String getProfileImagePreSignedUrl(UserDomain userDomain) {
    if (userDomain != null && userDomain.getS3ProfileImageObjectKey() != null) {
      return s3Service
          .getPreSignedUrlForImage(userDomain.getS3ProfileImageObjectKey(), userDomain.getUserId())
          .preSignedUrl();
    }
    throw new ApiException(
        HttpStatus.BAD_REQUEST,
        String.format(
            "User (%d) or User (%d)'s profile image not found.",
            userDomain.getUserId(), userDomain.getUserId()));
  }

  private String getPostImagePreSignedUrl(String s3ImageUrl, Long userId, Long postId) {
    if (s3ImageUrl != null && !s3ImageUrl.isBlank()) {
      return s3Service.getDownloadURL(s3ImageUrl, postId).preSignedUrl();
    }
    throw new ApiException(
        HttpStatus.BAD_REQUEST,
        String.format("s3ImageUrl (%s), Post (%d)'s postImage not found", s3ImageUrl, postId));
  }

  @Override
  public PostDomain getPost(Long postId) {
    return postRepository
        .findByPostId(postId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.NOT_FOUND, String.format("Post with id %s not found", postId)));
  }

  private InterviewResponseDto getInterviewResponseDtoByDomain(InterviewDomain interviewDomain) {
    if (interviewDomain == null) {
      return null;
    }
    return interviewService.getInterviewResponseDtoByDomain(interviewDomain);
  }

  private OverallEvaluationResponseDto getOverallEvaluationResponseDtoByDomain(
      OverallEvaluationDomain overallEvaluationDomain) {
    if (overallEvaluationDomain == null) {
      return null;
    }
    return evaluationService.getOverallEvaluationByInterviewId(
        overallEvaluationDomain.getInterviewDomain().getInterviewId());
  }

  private UserDomain getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.NOT_FOUND, String.format("(%d): User Not Found", userId)));
  }

  @Override
  public Slice<PostCreateResponseDto> getPostBySubscription(Long userId, Pageable pageable) {
    List<SubscriptionDomain> subscriptionDomains = subscriptionRepository.findByUserId(userId);
    List<Long> subscribedJobsIds =
        subscriptionDomains.stream().map((e) -> e.getJob().getJobId()).toList();

    Slice<PostDomain> postDomains =
        postRepository.findByJobIdsPageable(subscribedJobsIds, pageable);

    return postDomains.map(
        (e) ->
            postConverter.fromDomainToCreateResponseDto(
                e,
                getInterviewResponseDtoByDomain(e.getInterviewDomain()),
                getOverallEvaluationResponseDtoByDomain(e.getOverallEvaluationDomain()),
                e.getAuthorDomain().getS3ProfileImageObjectKey() == null
                    ? null
                    : getProfileImagePreSignedUrl(e.getAuthorDomain()),
                e.getS3ImageUrl() == null
                    ? null
                    : getPostImagePreSignedUrl(e.getS3ImageUrl(), userId, e.getPostId())));
  }

  @Override
  public Slice<PostCreateResponseDto> searchPostByContent(
      Long userId, String keyword, Pageable pageable) {
    Slice<PostDomain> postDomains = postRepository.searchByContentPageable(keyword, pageable);
    return postDomains.map(
        (e) ->
            postConverter.fromDomainToCreateResponseDto(
                e,
                getInterviewResponseDtoByDomain(e.getInterviewDomain()),
                getOverallEvaluationResponseDtoByDomain(e.getOverallEvaluationDomain()),
                e.getAuthorDomain().getS3ProfileImageObjectKey() == null
                    ? null
                    : getProfileImagePreSignedUrl(e.getAuthorDomain()),
                e.getS3ImageUrl() == null
                    ? null
                    : getPostImagePreSignedUrl(e.getS3ImageUrl(), userId, e.getPostId())));
  }
}
