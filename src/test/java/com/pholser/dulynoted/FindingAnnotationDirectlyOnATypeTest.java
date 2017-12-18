package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnATypeTest {
    @Test void directlyOnType() {
        A found =
            new DirectPresenceOnType(DirectlyOnType.class).find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(2, found.value());
    }

    @Test void missingFromDirectlyOnType() {
        assertNull(
            new DirectPresenceOnType(DirectlyOnType.class).find(B.class)
                .orElse(null));
    }

    @A(2) private static final class DirectlyOnType {
    }
}
