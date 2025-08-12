package com.pholser.annogami.annotated;

import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import com.pholser.annogami.annotations.Unit;

@Atom(3) @Iota(3) @Unit(6)
public class AnnotationsGalore
  extends SuperAnnotationsGalore
  implements IAnnotationsGalore1, IAnnotationsGalore2 {

  @Atom(7) private int i;

  @Atom(2) @Iota(2) public AnnotationsGalore(@Atom(1) int i) {
    @Iota(9) class Local {
    }
  }

  @Atom(5) @Iota(5) public void foo(@Atom(4) int i) {
    @Atom(8) class Local {
    }
  }

  @Atom(20)
  @Override public AnnotationsGalore self(int i, Object o) {
    return this;
  }
}
