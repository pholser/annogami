package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnAFieldTest {
    @Test void directlyOnField() throws Exception {
        A found =
            new DirectPresence(DirectlyOnField.class.getDeclaredField("i"))
            .find(A.class)
            .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(4, found.value());
    }

    @Test void missingFromDirectlyOnField() throws Exception {
        assertNull(
            new DirectPresence(DirectlyOnField.class.getDeclaredField("i"))
            .find(B.class)
            .orElse(null));
    }

    static final class DirectlyOnField {
        @A(4) int i;
    }
}
