package org.richardstallman.dvback.client.firebase.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Getter
@Builder
public class FcmTokenDomain {

  Long fcmTokenId;
  UserDomain userDomain;
  String token;
}
