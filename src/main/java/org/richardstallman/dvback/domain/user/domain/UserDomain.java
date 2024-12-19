package org.richardstallman.dvback.domain.user.domain;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

@Getter
@Builder
public class UserDomain {

  private Long userId;
  private String socialId;
  private String email;
  private String username;
  private String name;
  private String nickname;
  private String s3ProfileImageObjectKey;
  private Boolean leave;
  private CommonConstants.Gender gender;
  private LocalDate birthdate;
  private JobDomain job;
}
