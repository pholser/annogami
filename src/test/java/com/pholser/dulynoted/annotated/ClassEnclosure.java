package com.pholser.dulynoted.annotated;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.Unit;

@Unit(-4) @Unit(-9) @Atom(-3) @Iota(-3)
public class ClassEnclosure {
  @Unit(-3) @Atom(-2)
  public static class Enclosed1 {
    @Unit(-2) @Atom(-1) public Enclosed1() {
      @Unit(-1) class A {
      }
    }

    @Unit(-7) @Iota(-2)
    public class Enclosed2 {
      @Unit(-8) @Unit(-6) @Iota(-1) public void foo() {
        @Unit(-5) class B {
        }
      }
    }
  }
}
