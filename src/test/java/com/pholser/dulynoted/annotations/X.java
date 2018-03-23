package com.pholser.dulynoted.annotations;

public class X {
    @Atom(1) int i;

    @Atom(2) @Iota(3) void foo() {
    }

    @Particle(4) String s;

    @Particle(5) @Particle(6) @Unit(7) @Unit(8)
    void bar() {
    }
}
