package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyMetaPresentOnEnclosuresTest {
    @Test void packageLevel() throws Exception {
        A found =
            new DirectMetaPresenceEnclosing(
                getClass().getDeclaredMethod("packageLevel"))
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(8, found.value());
    }

    @Test void nestedClass() {
        A found =
            new DirectMetaPresenceEnclosing(I1.class)
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(100, found.value());
    }

    @Test void fieldOnNestedClass() throws Exception {
        A found =
            new DirectMetaPresenceEnclosing(I1.class.getDeclaredField("I"))
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(100, found.value());
    }

    @Test void methodOnNestedClass() throws Exception {
        A found =
            new DirectMetaPresenceEnclosing(I1.class.getDeclaredMethod("a"))
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(101, found.value());
    }

    @Test void doublyNestedClass() {
        A found =
            new DirectMetaPresenceEnclosing(I1.I1Level1.class)
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(102, found.value());
    }

    @Test void fieldOnDoublyNestedClass() throws Exception {
        A found =
            new DirectMetaPresenceEnclosing(
                I1.I1Level1.class.getDeclaredField("J"))
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(103, found.value());
    }

    @Test void methodOnDoublyNestedClass() throws Exception {
        A found =
            new DirectMetaPresenceEnclosing(
                I1.I1Level1.class.getDeclaredMethod("b"))
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(102, found.value());
    }

    @A(100) private interface I1 {
        int I = 0;

        @A(101) void a();

        @A(102) interface I1Level1 {
            @A(103) int J = 0;

            void b();
        }
    }
}
