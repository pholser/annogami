package com.pholser.dulynoted.annotations;

@Unit(1)
@Unit(2)
public class X {
    @Unit(3) @Marker int i;

    @Aggregate({@Unit(4), @Unit(5)}) void foo() {
    }

    @Aggregate(@Unit(6)) void bar() {
    }
}
