package com.pholser.annogami;

import static com.pholser.annogami.Presences.DIRECT;

/**
 * An object that can find annotations that are either "directly present"
 * on a program element, or are directly present on any of the annotations
 * that are directly present on the element.
 */
public final class MetaDirect extends AbstractMeta<Direct> {
  MetaDirect() {
    super(DIRECT);
  }
}
