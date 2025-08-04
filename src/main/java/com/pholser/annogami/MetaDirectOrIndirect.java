package com.pholser.annogami;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;

public final class MetaDirectOrIndirect
  extends AbstractMetaRepeatable<DirectOrIndirect> {

  MetaDirectOrIndirect() {
    super(DIRECT_OR_INDIRECT, DIRECT);
  }
}
