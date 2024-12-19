package org.richardstallman.dvback.domain.answer.service;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.answer.converter.AnswerConverter;
import org.richardstallman.dvback.domain.answer.domain.AnswerDomain;
import org.richardstallman.dvback.domain.answer.domain.request.AnswerEvaluationRequestDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationCriteriaDto;
import org.richardstallman.dvback.domain.answer.domain.request.evaluation.AnswerEvaluationScoreDto;
import org.richardstallman.dvback.domain.answer.repository.AnswerRepository;
import org.richardstallman.dvback.domain.evaluation.converter.AnswerEvaluationConverter;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.answer.AnswerEvaluationScoreDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.OverallEvaluationDomain;
import org.richardstallman.dvback.domain.evaluation.domain.overall.request.OverallEvaluationRequestDto;
import org.richardstallman.dvback.domain.evaluation.repository.answer.AnswerEvaluationRepository;
import org.richardstallman.dvback.domain.evaluation.repository.answer.score.AnswerEvaluationScoreRepository;
import org.richardstallman.dvback.domain.evaluation.repository.overall.OverallEvaluationRepository;
import org.richardstallman.dvback.domain.evaluation.service.EvaluationService;
import org.richardstallman.dvback.domain.interview.domain.InterviewDomain;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

  private final AnswerRepository answerRepository;
  private final AnswerConverter answerConverter;
  private final OverallEvaluationRepository overallEvaluationRepository;
  private final AnswerEvaluationRepository answerEvaluationRepository;
  private final AnswerEvaluationScoreRepository answerEvaluationScoreRepository;
  private final AnswerEvaluationConverter answerEvaluationConverter;
  private final EvaluationService evaluationService;

  @Override
  public void saveAnswerEvaluations(AnswerEvaluationRequestDto dto) {
    log.info("Start saving Answer and Evaluations for Question ID: {}", dto.questionId());
    saveAnswer(dto);
    log.info("Successfully saved Answer and Evaluations for Question ID: {}", dto.questionId());
  }

  private void saveAnswer(AnswerEvaluationRequestDto dto) {
    AnswerDomain previousAnswer = getAnswerDomainFromQuestionId(dto);
    saveAnswerEvaluation(dto, previousAnswer);
  }

  private void saveAnswerEvaluation(AnswerEvaluationRequestDto dto, AnswerDomain answerDomain) {
    getOverallEvaluationDomainFromInterview(
        answerDomain.getQuestionDomain().getInterviewDomain(), dto, answerDomain);
  }

  private void saveAnswerEvaluationScores(
      AnswerEvaluationDomain answerEvaluationDomain, @NotNull AnswerEvaluationScoreDto scores) {
    List<AnswerEvaluationScoreDomain> scoreDomains = new ArrayList<>();
    Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> textScores = new HashMap<>();
    if (answerEvaluationDomain.getOverallEvaluationDomain().getInterviewDomain().getInterviewType()
        == InterviewType.PERSONAL) {

      textScores = scores.textScores().toPersonalMap();
    } else if (answerEvaluationDomain
            .getOverallEvaluationDomain()
            .getInterviewDomain()
            .getInterviewType()
        == InterviewType.TECHNICAL) {
      textScores = scores.textScores().toTechnicalMap();
    }

    for (Map.Entry<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> entry :
        textScores.entrySet()) {
      scoreDomains.add(
          buildAnswerEvaluationScore(
              entry.getKey(), entry.getValue(), answerEvaluationDomain, AnswerEvaluationType.TEXT));
    }
    if (answerEvaluationDomain
            .getOverallEvaluationDomain()
            .getInterviewDomain()
            .getInterviewMethod()
        == InterviewMethod.VOICE) {
      Map<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> voiceScores =
          scores.voiceScores().toMap();

      for (Map.Entry<AnswerEvaluationScore, AnswerEvaluationCriteriaDto> entry :
          voiceScores.entrySet()) {
        scoreDomains.add(
            buildAnswerEvaluationScore(
                entry.getKey(),
                entry.getValue(),
                answerEvaluationDomain,
                AnswerEvaluationType.VOICE));
      }
    }
    answerEvaluationScoreRepository.saveAll(scoreDomains);

    checkFinal(answerEvaluationDomain);
  }

  private void checkFinal(AnswerEvaluationDomain answerEvaluationDomain) {
    InterviewDomain interviewDomain =
        answerEvaluationDomain.getQuestionDomain().getInterviewDomain();
    long count = answerEvaluationRepository.countByInterviewId(interviewDomain.getInterviewId());
    log.info("answer evaluation saved {}/{}", count, interviewDomain.getQuestionCount());
    if (count == interviewDomain.getQuestionCount()) {
      log.info("answer evaluation finished. start request overall evaluation");
      evaluationService.getOverallEvaluation(
          new OverallEvaluationRequestDto(interviewDomain.getInterviewId()),
          interviewDomain.getUserDomain().getUserId());
    }
  }

  private AnswerEvaluationScoreDomain buildAnswerEvaluationScore(
      AnswerEvaluationScore scoreName,
      AnswerEvaluationCriteriaDto scoreDetail,
      AnswerEvaluationDomain answerEvaluationDomain,
      AnswerEvaluationType type) {

    return AnswerEvaluationScoreDomain.builder()
        .answerEvaluationScoreName(scoreName)
        .score(scoreDetail.score())
        .rationale(scoreDetail.rationale())
        .answerEvaluationDomain(answerEvaluationDomain)
        .answerEvaluationType(type)
        .build();
  }

  private AnswerDomain getAnswerDomainFromQuestionId(
      AnswerEvaluationRequestDto answerEvaluationRequestDto) {
    AnswerDomain answerDomain =
        answerRepository.findByQuestionId(answerEvaluationRequestDto.questionId());
    return answerRepository.update(
        answerConverter.fromEvaluationRequestDtoToDomain(answerDomain, answerEvaluationRequestDto));
  }

  private void getOverallEvaluationDomainFromInterview(
      InterviewDomain interviewDomain, AnswerEvaluationRequestDto dto, AnswerDomain answerDomain) {
    OverallEvaluationDomain overallEvaluationDomain =
        overallEvaluationRepository.findByInterviewId(interviewDomain.getInterviewId());
    log.info(
        "overallEvaluationDomain: {}",
        overallEvaluationDomain == null ? null : overallEvaluationDomain.toString());
    if (overallEvaluationDomain == null) {
      overallEvaluationDomain =
          overallEvaluationRepository.save(
              OverallEvaluationDomain.builder().interviewDomain(interviewDomain).build());
    }
    saveAnswerEvaluations(dto, answerDomain, overallEvaluationDomain);
  }

  private void saveAnswerEvaluations(
      AnswerEvaluationRequestDto dto,
      AnswerDomain answerDomain,
      OverallEvaluationDomain overallEvaluationDomain) {
    AnswerEvaluationDomain answerEvaluationDomain =
        answerEvaluationRepository.save(
            answerEvaluationConverter.sttEvaluationFeedbackDomainToDomain(
                dto.answer().feedback(), answerDomain, overallEvaluationDomain));
    saveAnswerEvaluationScores(answerEvaluationDomain, dto.answer().scores());
  }
}
