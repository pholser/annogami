package com.pholser.dulynoted.annotated;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.Unit;

@Atom(3) @Iota(3) @Unit(6)
public class AnnotationsGalore {
  @Atom(7) private int i;

  @Atom(2) @Iota(2) public AnnotationsGalore(@Atom(1) int i) {
    @Iota(9) class Local {
    }
  }

  @Atom(5) @Iota(5) public void foo(@Atom(4) int i) {
    @Atom(8) class Local {
    }
  }
}
