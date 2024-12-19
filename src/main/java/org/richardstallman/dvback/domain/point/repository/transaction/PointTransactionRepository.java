package org.richardstallman.dvback.domain.point.repository.transaction;

import java.util.List;
import org.richardstallman.dvback.domain.point.domain.PointTransactionDomain;

public interface PointTransactionRepository {

  PointTransactionDomain save(PointTransactionDomain pointTransactionDomain);

  List<PointTransactionDomain> findByUserId(Long userId);
}
