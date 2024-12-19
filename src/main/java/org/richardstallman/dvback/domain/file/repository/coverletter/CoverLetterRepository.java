package org.richardstallman.dvback.domain.file.repository.coverletter;

import java.util.List;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;

public interface CoverLetterRepository {

  CoverLetterDomain save(CoverLetterDomain coverLetterDomain);

  CoverLetterDomain findByCoverLetterId(Long coverLetterId);

  List<CoverLetterDomain> findCoverLetterListByUserId(Long userId);
}
