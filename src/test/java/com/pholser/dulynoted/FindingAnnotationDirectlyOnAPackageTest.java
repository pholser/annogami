package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnAPackageTest {
    @Test void directlyOnPackage() {
        A found =
            new DirectPresence(getClass().getPackage())
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(8, found.value());
    }

    @Test void missingFromDirectlyOnPackage() {
        assertNull(
            new DirectPresence(getClass().getPackage())
                .find(B.class)
                .orElse(null));
    }
}
