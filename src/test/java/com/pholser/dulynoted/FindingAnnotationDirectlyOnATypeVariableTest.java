package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.B;
import com.pholser.dulynoted.annotations.C;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnATypeVariableTest {
    @Test void directlyOnClassTypeVariable() {
        C found =
            new DirectPresence(
                DirectlyOnTypeVariable.class.getTypeParameters()[0])
                .find(C.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(9, found.value());
    }

    @Test void missingFromDirectlyOnClassTypeVariable() {
        assertNull(
            new DirectPresence(
                DirectlyOnTypeVariable.class.getTypeParameters()[0])
                .find(B.class)
                .orElse(null));
    }

    @Test void directlyOnConstructorTypeVariable() throws Exception {
        C found =
            new DirectPresence(
                DirectlyOnTypeVariable.class
                    .getDeclaredConstructor(Object.class)
                    .getTypeParameters()[0])
            .find(C.class)
            .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(10, found.value());
    }

    @Test void missingFromDirectlyOnConstructorTypeVariable() throws Exception {
        assertNull(
            new DirectPresence(
                DirectlyOnTypeVariable.class
                    .getDeclaredConstructor(Object.class)
                    .getTypeParameters()[0])
            .find(B.class)
            .orElse(null));
    }

    @Test void directlyOnMethodTypeVariable() throws Exception {
        C found =
            new DirectPresence(
                DirectlyOnTypeVariable.class
                    .getDeclaredMethod("foo", Object.class)
                    .getTypeParameters()[0])
                .find(C.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(11, found.value());
    }

    @Test void missingFromDirectlyOnMethodTypeVariable() throws Exception {
        assertNull(
            new DirectPresence(
                DirectlyOnTypeVariable.class
                    .getDeclaredMethod("foo", Object.class)
                    .getTypeParameters()[0])
                .find(B.class)
                .orElse(null));
    }

    private static final class DirectlyOnTypeVariable<@C(9) T> {
        <@C(10) A> DirectlyOnTypeVariable(A thing) {
        }

        <@C(11) B> void foo(B bar) {
        }
    }
}
