package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnAnAnnotationTypeTest {
    @Test void directlyOnAnnotationType() {
        A found =
            new DirectPresenceOnAnnotationType(DirectlyOnAnnotationType.class)
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(7, found.value());
    }

    @Test void missingFromDirectlyOnAnnotationType() {
        assertNull(
            new DirectPresenceOnAnnotationType(DirectlyOnAnnotationType.class)
                .find(B.class)
                .orElse(null));
    }

    @A(7) @interface DirectlyOnAnnotationType {
    }
}
