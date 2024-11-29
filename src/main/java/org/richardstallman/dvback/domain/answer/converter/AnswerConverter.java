package org.richardstallman.dvback.domain.answer.converter;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerEvaluationRequestDto;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerPreviousRequestDto;
import org.richardstallman.dvback.domain.answer.entity.AnswerEntity;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.richardstallman.dvback.domain.question.converter.QuestionConverter;
import org.richardstallman.dvback.domain.question.domain.QuestionDomain;
import org.richardstallman.dvback.domain.question.domain.external.QuestionExternalSttAnswerRequestDomain;
import org.richardstallman.dvback.domain.question.domain.external.request.QuestionExternalSttRequestDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class AnswerConverter {

  private final QuestionConverter questionConverter;

  public AnswerConverter(@Lazy QuestionConverter questionConverter) {
    this.questionConverter = questionConverter;
  }

  public AnswerEntity fromDomainToEntity(AnswerDomain answerDomain) {
    return new AnswerEntity(
        answerDomain.getAnswerId(),
        questionConverter.fromDomainToEntity(answerDomain.getQuestionDomain()),
        answerDomain.getAnswerText(),
        answerDomain.getS3AudioUrl(),
        answerDomain.getS3VideoUrl());
  }

  public AnswerEntity fromDomainToEntityWhenCreate(AnswerDomain answerDomain) {
    return new AnswerEntity(
        questionConverter.fromDomainToEntity(answerDomain.getQuestionDomain()),
        answerDomain.getAnswerText(),
        answerDomain.getS3AudioUrl(),
        answerDomain.getS3VideoUrl());
  }

  public AnswerDomain fromEntityToDomain(AnswerEntity answerEntity) {
    return AnswerDomain.builder()
        .answerId(answerEntity.getAnswerId())
        .questionDomain(questionConverter.fromEntityToDomain(answerEntity.getQuestion()))
        .answerText(answerEntity.getAnswerText())
        .s3AudioUrl(answerEntity.getS3AudioUrl())
        .s3VideoUrl(answerEntity.getS3VideoUrl())
        .build();
  }

  public AnswerDomain fromPreviousRequestDtoToDomain(
      AnswerPreviousRequestDto answerPreviousRequestDto, QuestionDomain questionDomain) {
    return AnswerDomain.builder()
        .questionDomain(questionDomain)
        .answerText(answerPreviousRequestDto.answerText())
        .s3AudioUrl(answerPreviousRequestDto.s3AudioUrl())
        .s3VideoUrl(answerPreviousRequestDto.s3VideoUrl())
        .build();
  }

  public QuestionExternalSttRequestDto fromPreviousRequestDtoToQuestionExternalSttRequestDto(
      @NotNull InterviewDomain interviewDomain, @NotNull AnswerPreviousRequestDto answer) {
    return new QuestionExternalSttRequestDto(
        interviewDomain.getInterviewMethod().getPythonFormat(),
        interviewDomain.getUserDomain().getId(),
        new QuestionExternalSttAnswerRequestDomain(
            answer.answerText(), answer.s3AudioUrl(), answer.s3VideoUrl()));
  }

  public AnswerDomain fromSttEvaluationRequestDtoToDomain(
      AnswerEvaluationRequestDto answerEvaluationRequestDto, AnswerDomain previousAnswer) {
    return AnswerDomain.builder()
        .answerId(previousAnswer.getAnswerId())
        .questionDomain(previousAnswer.getQuestionDomain())
        .answerText(answerEvaluationRequestDto.answer().answerText())
        .s3AudioUrl(answerEvaluationRequestDto.answer().s3AudioUrl())
        .s3VideoUrl(answerEvaluationRequestDto.answer().s3VideoUrl())
        .build();
  }
}
