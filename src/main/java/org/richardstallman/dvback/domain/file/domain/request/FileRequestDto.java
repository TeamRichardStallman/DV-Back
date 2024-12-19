package org.richardstallman.dvback.domain.file.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.FileType;

@Getter
@AllArgsConstructor
public class FileRequestDto {

  private FileType type;
  private String filePath;
}
