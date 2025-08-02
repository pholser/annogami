package com.pholser.dulynoted;

import static com.pholser.dulynoted.Presences.ASSOCIATED;
import static com.pholser.dulynoted.Presences.PRESENT;

public final class MetaAssociated extends AbstractMetaRepeatable<Associated> {
  MetaAssociated() {
    super(ASSOCIATED, PRESENT);
  }
}
