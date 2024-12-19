package org.richardstallman.dvback.domain.file.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Builder
@Getter
public class CoverLetterDomain {

  private final Long coverLetterId;
  private final UserDomain userDomain;
  private String fileName;
  private String s3FileUrl;
}
