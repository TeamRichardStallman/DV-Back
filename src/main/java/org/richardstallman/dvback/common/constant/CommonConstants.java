package org.richardstallman.dvback.common.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommonConstants {

  public enum InterviewStatus {
    INITIAL, // 최초 정보 입력 상태
    FILE_UPLOADED, // 자소서 및 파일 업로드 완료 상태
    WAITING_FOR_QUESTION, // 질문 생성 요청 후 대기 상태
    READY, // 면접 시작 가능 상태
    IN_PROGRESS, // 면접 진행 중 상태
    WAITING_FOR_FEEDBACK, // 피드백 생성 요청 후 대기 상태
    COMPLETED // 피드백 완료 상태
  }

  @Getter
  @AllArgsConstructor
  public enum InterviewType {
    TECHNICAL("technical", "기술"), // 기술 면접
    PERSONAL("personal", "인성"); // 인성 면접

    private final String pythonFormat;
    private final String koreanName;
  }

  @Getter
  @AllArgsConstructor
  public enum InterviewMode {
    REAL("real", "실전"), // 실전 면접 모드
    GENERAL("general", "모의"); // 일반/모의 면접 모드

    private final String pythonFormat;
    private final String koreanName;
  }

  @Getter
  @AllArgsConstructor
  public enum InterviewMethod {
    CHAT("chat", "채팅"), // 채팅 면접
    VOICE("voice", "음성"), // 음성 면접
    VIDEO("video", "영상"); // 영상 면접

    private final String pythonFormat;
    private final String koreanName;
  }

  @AllArgsConstructor
  @Getter
  public enum ResponseCode {
    SUCCESS(200, "SUCCESS"),
    ACCEPTED(202, "ACCEPTED"),
    FAIL(400, "FAILED"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private final int code;
    private final String message;
  }

  @Getter
  @AllArgsConstructor
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  public enum EvaluationCriteria {
    // technical text
    JOB_FIT("job_fit"),
    GROWTH_POTENTIAL("growth_potential"),
    WORK_ATTITUDE("work_attitude"),
    TECHNICAL_DEPTH("technical_depth"),
    // personal text
    COMPANY_FIT("company_fit"),
    ADAPTABILITY("adaptability"),
    INTERPERSONAL_SKILLS("interpersonal_skills"),
    GROWTH_ATTITUDE("growth_attitude"),
    FLUENCY("fluency"),
    CLARITY("clarity"),
    WORD_REPETITION("word_repetition");

    private final String pythonFormat;
  }

  public enum AnswerEvaluationScore {
    // technical text scores
    APPROPRIATE_RESPONSE,
    LOGICAL_FLOW,
    KEY_TERMS,
    CONSISTENCY,
    GRAMMATICAL_ERRORS,
    // personal text scores
    TEAMWORK,
    COMMUNICATION,
    PROBLEM_SOLVING,
    ACCOUNTABILITY,
    GROWTH_MINDSET,
    // voice scores
    WPM,
    STUTTER,
    PRONUNCIATION
  }

  public enum PointTransactionType {
    DEPOSIT, // 적립
    WITHDRAWAL // 사용
  }

  @Getter
  @AllArgsConstructor
  public enum TicketTransactionMethod {
    COUPON("쿠폰"),
    EVENT("이벤트"),
    CHAT("채팅 면접"),
    VOICE("음성 면접");

    private final String koreanName;
  }

  @Getter
  @AllArgsConstructor
  public enum TicketTransactionType {
    CHARGE("충전"), // 충전
    USE("사용"); // 사용

    private final String koreanName;
  }

  @Getter
  @AllArgsConstructor
  public enum InterviewAssetType {
    CHAT("채팅"), // 채팅 면접
    VOICE("음성"), // 음성 면접
    VIDEO("영상"); // 영상 면접

    private final String koreanName;
  }

  @Getter
  @AllArgsConstructor
  public enum Gender {
    WOMAN, // 여성
    MAN // 남성
  }

  @Getter
  @AllArgsConstructor
  public enum FileType {
    COVER_LETTER("cover-letter"),
    RESUME("resume"),
    PORTFOLIO("portfolio"),
    AUDIO_ANSWER("audio-answer"),
    AUDIO_QUESTION("audio-question"),
    VIDEO_ANSWER("video-answer"),
    VIDEO_QUESTION("video-question"),
    PROFILE_IMAGE("profile-image");

    private final String folderName;
  }

  @Getter
  @AllArgsConstructor
  public enum PostType {
    EVALUATION("평가 공유"),
    INTERVIEW("면접 공유"),
    POST("게시글");

    private final String koreanName;
  }

  public enum AnswerEvaluationType {
    TEXT,
    VOICE,
    VIDEO
  }
}
