package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnAConstructorTest {
    @Test void directlyOnConstructor() throws Exception {
        A found =
            new DirectPresence(
                DirectlyOnConstructor.class.getDeclaredConstructor())
            .find(A.class)
            .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(1, found.value());
    }

    @Test void missingFromDirectlyOnConstructor() throws Exception {
        assertNull(
            new DirectPresence(
                DirectlyOnConstructor.class.getDeclaredConstructor())
            .find(B.class)
            .orElse(null));
    }

    static final class DirectlyOnConstructor {
        @A(1) DirectlyOnConstructor() {
        }
    }
}
