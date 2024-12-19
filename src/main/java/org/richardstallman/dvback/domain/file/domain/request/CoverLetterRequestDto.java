package org.richardstallman.dvback.domain.file.domain.request;

import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;

@Getter
public class CoverLetterRequestDto extends FileRequestDto {

  public CoverLetterRequestDto(FileType type, String filePath) {
    super(type, filePath);
  }
}
