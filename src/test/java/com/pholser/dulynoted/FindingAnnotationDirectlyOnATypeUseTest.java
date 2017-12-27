package com.pholser.dulynoted;

import java.io.IOException;

import com.pholser.dulynoted.annotations.B;
import com.pholser.dulynoted.annotations.C;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyOnATypeUseTest {
    @Test void directlyOnTypeUse() throws Exception {
        C found =
            new DirectPresenceOnTypeUse(
                DirectlyOnTypeUse.class
                    .getDeclaredMethod("foo")
                    .getAnnotatedExceptionTypes()[0])
                .find(C.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(12, found.value());
    }

    @Test void missingFromDirectlyOnTypeUse() {
        assertNull(
            new DirectPresenceOnType(DirectlyOnTypeUse.class).find(B.class)
                .orElse(null));
    }

    private static final class DirectlyOnTypeUse {
        void foo() throws @C(12) IOException {
        }
    }
}
