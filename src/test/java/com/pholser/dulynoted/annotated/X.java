package com.pholser.dulynoted.annotated;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.Unit;

@Atom(9)
public class X extends SuperX {
  @Atom(1) int i;

  @Atom(2) @Iota(3) void foo() {
  }

  @Particle(4) String s;

  @Particle(5) @Particle(6)
  @Unit(7) @Unit(8)
  void bar() {
  }
}
