package com.pholser.annogami;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;

/**
 * An object that can find annotations that are either "directly present"
 * or "indirectly present" on a program element, or are directly/indirectly
 * present on any of the annotations that are directly/indirectly present on
 * the element.
 */
public final class MetaDirectOrIndirect
  extends AbstractMetaRepeatable<DirectOrIndirect> {

  MetaDirectOrIndirect() {
    super(DIRECT_OR_INDIRECT, DIRECT);
  }
}
