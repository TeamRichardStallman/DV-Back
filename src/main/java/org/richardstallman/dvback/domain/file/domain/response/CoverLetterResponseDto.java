package org.richardstallman.dvback.domain.file.domain.response;

import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;

@Getter
public class CoverLetterResponseDto extends FileResponseDto {

  public CoverLetterResponseDto(Long fileId, String fileName, String s3FileUrl) {
    super(fileId, FileType.COVER_LETTER, fileName, s3FileUrl);
  }
}
