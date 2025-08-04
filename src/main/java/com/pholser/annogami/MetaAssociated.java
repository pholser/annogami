package com.pholser.annogami;

import static com.pholser.annogami.Presences.ASSOCIATED;
import static com.pholser.annogami.Presences.PRESENT;

public final class MetaAssociated extends AbstractMetaRepeatable<Associated> {
  MetaAssociated() {
    super(ASSOCIATED, PRESENT);
  }
}
