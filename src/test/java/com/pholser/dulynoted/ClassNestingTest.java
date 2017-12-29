package com.pholser.dulynoted;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

class ClassNestingTest {
    @Test void object() {
        assertEquals(
            singletonList(Object.class),
            new ClassNesting(Object.class).layers());
    }

    @Test void oneLevel() {
        assertEquals(
            asList(A.class, getClass()),
            new ClassNesting(A.class).layers());
    }

    @Test void twoLevels() {
        assertEquals(
            asList(A.B.class, A.class, getClass()),
            new ClassNesting(A.B.class).layers());
    }

    @Test void threeLevels() {
        assertEquals(
            asList(A.B.C.class, A.B.class, A.class, getClass()),
            new ClassNesting(A.B.C.class).layers());
    }

    private static final class A {
        private static final class B {
            private static final class C {
            }
        }
    }
}
