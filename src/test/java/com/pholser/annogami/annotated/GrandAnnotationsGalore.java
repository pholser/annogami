package com.pholser.annogami.annotated;

import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;

@Iota(-11)
public class GrandAnnotationsGalore
  implements IAnnotationsGalore2, IAnnotationsGalore5 {

  @Atom(22)
  @Override public GrandAnnotationsGalore self(int i, Object o) {
    return this;
  }
}
