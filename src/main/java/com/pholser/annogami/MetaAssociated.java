package com.pholser.annogami;

import static com.pholser.annogami.Presences.ASSOCIATED;
import static com.pholser.annogami.Presences.PRESENT;

/**
 * An object that can find annotations that are either "associated" on a
 * program element, or are associated on any of the annotations that are
 * present on the element. Unless the program element is a class, and the
 * annotations you're interested in are {@linkplain
 * java.lang.annotation.Inherited inherited}, this object should behave like
 * {@link MetaDirectOrIndirect}.
 */
public final class MetaAssociated extends AbstractMetaRepeatable<Associated> {
  MetaAssociated() {
    super(ASSOCIATED, PRESENT);
  }
}
