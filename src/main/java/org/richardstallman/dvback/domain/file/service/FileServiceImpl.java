package org.richardstallman.dvback.domain.file.service;

import java.nio.file.Paths;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

  @Override
  public String getFileName(String fileUrl) {
    if (fileUrl == null || fileUrl.isEmpty()) {
      return null;
    }
    return Paths.get(fileUrl).getFileName().toString();
  }
}
