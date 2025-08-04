package com.pholser.annogami.annotated;

import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;

@Iota(-10)
public class SuperAnnotationsGalore
  extends GrandAnnotationsGalore
    implements IAnnotationsGalore1, IAnnotationsGalore3 {

  @Atom(21)
  @Override public SuperAnnotationsGalore self(int i, Object o) {
    return this;
  }
}
