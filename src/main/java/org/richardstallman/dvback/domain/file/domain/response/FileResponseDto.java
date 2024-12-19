package org.richardstallman.dvback.domain.file.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;

@Getter
@AllArgsConstructor
public class FileResponseDto {

  private Long fileId;
  private FileType type;
  private String fileName;
  private String s3FileUrl;
}
