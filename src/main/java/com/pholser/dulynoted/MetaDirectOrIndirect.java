package com.pholser.dulynoted;

import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.DIRECT_OR_INDIRECT;

public final class MetaDirectOrIndirect
  extends AbstractMetaRepeatable<DirectOrIndirect> {

  MetaDirectOrIndirect() {
    super(DIRECT_OR_INDIRECT, DIRECT);
  }
}
