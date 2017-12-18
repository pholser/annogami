package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnAMethodTest {
    @Test void directlyOnMethod() throws Exception {
        A found =
            new DirectPresenceOnMethod(
                DirectlyOnMethod.class.getDeclaredMethod("foo"))
            .find(A.class)
            .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(3, found.value());
    }

    @Test void missingFromDirectlyOnMethod() throws Exception {
        assertNull(
            new DirectPresenceOnMethod(
                DirectlyOnMethod.class.getDeclaredMethod("foo"))
            .find(B.class)
            .orElse(null));
    }

    static final class DirectlyOnMethod {
        @A(3) void foo() {
        }
    }
}
