package com.pholser.annogami;

import static com.pholser.annogami.Presences.PRESENT;

/**
 * An object that can find annotations that are either "present" on a program
 * element, or are present on any of the annotations that are present on the
 * element. Unless the program element is a class, and the annotations
 * you're interested in are {@linkplain java.lang.annotation.Inherited
 * inherited}, this object should behave like {@link MetaDirect}.
 */
public final class Meta extends AbstractMeta<Present> {
  Meta() {
    super(PRESENT);
  }
}
