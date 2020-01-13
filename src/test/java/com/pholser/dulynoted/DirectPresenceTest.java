package com.pholser.dulynoted;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;
import static org.junit.jupiter.api.Assertions.*;

class DirectPresenceTest {
    @Retention(RUNTIME)
    @interface A {
        int value();
    }

    @Retention(RUNTIME)
    @interface C {
        char value();
    }

    @Retention(RUNTIME)
    @interface Cs {
        Bs[] value();

        C[] cs() default {};

        String name() default "";
    }

    @Repeatable(Cs.class)
    @Retention(RUNTIME)
    @interface Bs {
        B[] value();

        int position() default 0;
    }

    @Repeatable(Bs.class)
    @Retention(RUNTIME)
    @interface B {
        String value();
    }

    @A(3)
    static class AHaver {
    }

    @B("1")
    static class SingleBHaver {
    }

    @B("2")
    @B("3")
    static class ManyBHaver {
    }

    @Bs({@B("4"), @B("5")})
    static class BsHaver {
    }

    @Bs({@B("6"), @B("7")})
    @Bs({@B("8"), @B("9")})
    static class ManyBsHaver {
    }

    @Test void singleDirect() {
        A a =
            new DirectPresence().find(A.class, AHaver.class)
                .orElseThrow(() -> new AssertionError("Cannot find A"));

        assertEquals(3, a.value());
    }

    @Test void singleRepeatableDirect() {
        B b =
            new DirectPresence().find(B.class, SingleBHaver.class)
                .orElseThrow(() -> new AssertionError("Cannot find B"));

        assertEquals("1", b.value());
    }

    @Test void singleRepeatableDirectContainer() {
        new DirectPresence().find(Bs.class, SingleBHaver.class)
            .ifPresent(bs -> fail("Should not have a Bs element"));
    }

    @Test void manyRepeatableDirectContainer() {
        Bs bs =
            new DirectPresence().find(Bs.class, ManyBHaver.class)
                .orElseThrow(() -> new AssertionError("Cannot find Bs"));

        B[] value = bs.value();
        assertEquals(2, value.length);
    }

    @Test void singleRepeatableContainerRepeatableContainer() {
        Bs bs =
            new DirectPresence().find(Bs.class, BsHaver.class)
                .orElseThrow(() -> new AssertionError("Cannot find Bs"));

        B[] value = bs.value();
        assertEquals(2, value.length);
    }

    @Test void singleRepeatableContainerDirectContainer() {
        new DirectPresence().find(Cs.class, BsHaver.class)
            .ifPresent(cs -> fail("Should not have a Cs element"));
    }

    @Test void manyRepeatableContainerInAContainer() {
        new DirectPresence().find(Bs.class, ManyBsHaver.class)
            .ifPresent(cs -> fail("Should not have a Bs element"));
    }

    @Test void manyRepeatableContainerInAContainerDirect() {
        Cs cs =
            new DirectPresence().find(Cs.class, ManyBsHaver.class)
                .orElseThrow(() -> new AssertionError("Cannot find Cs"));

        Bs[] value = cs.value();
        assertEquals(2, value.length);
    }
}
