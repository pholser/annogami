package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnAMethodParameterTest {
    @Test void directlyOnConstructorParameter() throws Exception {
        A found =
            new DirectPresence(
                DirectlyOnMethodParameter.class
                    .getDeclaredConstructor(int.class)
                    .getParameters()[0])
            .find(A.class)
            .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(5, found.value());
    }

    @Test void missingFromDirectlyOnConstructorParameter() throws Exception {
        assertNull(
            new DirectPresence(
                DirectlyOnMethodParameter.class
                    .getDeclaredConstructor(int.class)
                    .getParameters()[0])
            .find(B.class)
            .orElse(null));
    }

    @Test void directlyOnMethodParameter() throws Exception {
        A found =
            new DirectPresence(
                DirectlyOnMethodParameter.class
                    .getDeclaredMethod("foo", int.class)
                    .getParameters()[0])
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(6, found.value());
    }

    @Test void missingFromDirectlyOnMethodParameter() throws Exception {
        assertNull(
            new DirectPresence(
                DirectlyOnMethodParameter.class
                    .getDeclaredMethod("foo", int.class)
                    .getParameters()[0])
                .find(B.class)
                .orElse(null));
    }

    static final class DirectlyOnMethodParameter {
        DirectlyOnMethodParameter(@A(5) int i) {
        }

        void foo(@A(6) int i) {
        }
    }
}
