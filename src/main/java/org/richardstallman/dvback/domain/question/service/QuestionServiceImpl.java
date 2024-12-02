package org.richardstallman.dvback.domain.question.service;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.client.firebase.service.FirebaseMessagingService;
import org.richardstallman.dvback.client.python.PythonService;
import org.richardstallman.dvback.domain.answer.converter.AnswerConverter;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerPreviousRequestDto;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.interview.converter.InterviewConverter;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.interview.repository.InterviewRepository;
import org.richardstallman.dvback.domain.job.domain.JobDomain;
import org.richardstallman.dvback.domain.job.service.JobService;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionInitialRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionNextRequestDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionRequestListDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionResultDto;
import org.richardstallman.dvback.domain.question.domain.request.QuestionResultRequestDto;
import org.richardstallman.dvback.domain.question.domain.response.QuestionResponseDto;
import org.richardstallman.dvback.domain.question.repository.QuestionRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final JobService jobService;
  private final PythonService pythonService;
  private final InterviewConverter interviewConverter;
  private final InterviewRepository interviewRepository;
  private final QuestionConverter questionConverter;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final AnswerConverter answerConverter;
  private final FirebaseMessagingService firebaseMessagingService;

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void getQuestionList(QuestionRequestListDto questionRequestListDto, Long userId) {
    JobDomain jobDomain = jobService.findJobById(questionRequestListDto.jobId());
    QuestionExternalRequestDto questionExternalRequestDto =
        questionConverter.reactRequestToPythonRequest(
            userId, questionRequestListDto, jobDomain.getJobName());

    pythonService.requestQuestionList(
        questionExternalRequestDto, questionRequestListDto.interviewId());

    log.info("Request Question List To Python: Succeed.");
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionResponseDto getInitialQuestion(
      QuestionInitialRequestDto questionInitialRequestDto, Long userId) {
    InterviewDomain interviewDomain =
        interviewRepository.findById(questionInitialRequestDto.interviewId());

    List<QuestionDomain> questionDomains =
        questionRepository.findQuestionsByInterviewId(questionInitialRequestDto.interviewId());

    boolean hasNext = questionDomains.size() > 1;

    QuestionDomain firstQuestion = questionDomains.get(0);
    QuestionDomain nextQuestion = hasNext ? questionDomains.get(1) : null;

    return questionConverter.generateResponseDto(
        interviewConverter.fromDomainToQuestionResponseDto(interviewDomain),
        firstQuestion,
        nextQuestion,
        hasNext);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionResponseDto getNextQuestion(QuestionNextRequestDto questionNextRequestDto) {

    List<QuestionDomain> questions =
        questionRepository.findQuestionsByInterviewId(questionNextRequestDto.interviewId());

    InterviewDomain interviewDomain = getInterviewDomain(questionNextRequestDto.interviewId());

    QuestionDomain previousQuestion =
        findQuestionById(questions, questionNextRequestDto.answerQuestionId());
    QuestionDomain currentQuestion =
        questionNextRequestDto.nextQuestionId() == null
            ? null
            : findQuestionById(questions, questionNextRequestDto.nextQuestionId());
    QuestionDomain nextQuestion =
        questionNextRequestDto.nextQuestionId() == null
            ? null
            : findNextQuestion(questions, currentQuestion);
    boolean hasNext = nextQuestion != null;

    AnswerDomain answerDomain =
        answerRepository.save(
            answerConverter.fromPreviousRequestDtoToDomain(
                questionNextRequestDto.answer(), previousQuestion));

    checkAnswer(interviewDomain, answerDomain.getAnswerId(), questionNextRequestDto.answer());

    return questionConverter.fromQuestionExternalDomainToQuestionResponseDto(
        currentQuestion,
        interviewConverter.fromDomainToQuestionResponseDto(previousQuestion.getInterviewDomain()),
        nextQuestion,
        hasNext);
  }

  @Override
  public void saveCompletedQuestion(QuestionResultRequestDto questionResult) {
    if (questionResult.questions().isEmpty()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Question List is empty");
    }
    Long userId = questionResult.userId();
    Long interviewId = questionResult.interviewId();
    List<QuestionResultDto> questionResultDtos = questionResult.questions();
    InterviewDomain interviewDomain = getInterviewDomain(interviewId);

    saveQuestions(questionResultDtos, interviewDomain);

    firebaseMessagingService.sendNotification(
        userId, "면접 준비가 완료되었습니다.", questionResult.interviewId().toString());
  }

  private void saveQuestions(
      List<QuestionResultDto> questionResultDtos, InterviewDomain interviewDomain) {
    List<QuestionDomain> createdQuestions =
        questionResultDtos.stream()
            .map(e -> questionConverter.fromResultDtoToDomain(e, interviewDomain))
            .toList();
    questionRepository.saveAll(createdQuestions);
  }

  private void checkAnswer(
      @NotNull InterviewDomain interviewDomain,
      Long answerId,
      @NotNull AnswerPreviousRequestDto answer) {
    pythonService.sendAnswer(
        answerConverter.fromPreviousRequestDtoToQuestionExternalSttRequestDto(
            interviewDomain, answer),
        interviewDomain.getInterviewId(),
        answerId);
  }

  private QuestionDomain findQuestionById(List<QuestionDomain> questions, Long questionId) {
    return questions.stream()
        .filter(q -> q.getQuestionId().equals(questionId))
        .findFirst()
        .orElseThrow(
            () -> new ApiException(HttpStatus.BAD_REQUEST, questionId + " does not exist"));
  }

  private QuestionDomain findNextQuestion(
      List<QuestionDomain> questions, QuestionDomain currentQuestion) {
    int currentIndex = questions.indexOf(currentQuestion);
    return (currentIndex < questions.size() - 1) ? questions.get(currentIndex + 1) : null;
  }

  private InterviewDomain getInterviewDomain(Long interviewId) {
    return interviewRepository.findById(interviewId);
  }
}
