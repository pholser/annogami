package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.A;
import com.pholser.dulynoted.annotations.B;
import com.pholser.dulynoted.annotations.D;
import com.pholser.dulynoted.annotations.G;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindingAnnotationDirectlyMetaPresentOnATypeTest {
    @Test void directlyOnType() {
        A found =
            new DirectMetaPresence(DirectlyOnType.class)
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(14, found.value());
    }

    @Test void missingFromDirectlyOnType() {
        assertNull(
            new DirectMetaPresence(DirectlyOnType.class)
                .find(B.class)
                .orElse(null));
    }

    @Test void oneLevelMeta() {
        A found =
            new DirectMetaPresence(OneLevelMeta.class)
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(13, found.value());
    }

    @Test void missingFromOneLevelMeta() {
        assertNull(
            new DirectMetaPresence(OneLevelMeta.class)
                .find(B.class)
                .orElse(null));
    }

    @Test void twoLevelMeta() {
        A found =
            new DirectMetaPresence(TwoLevelMeta.class)
                .find(A.class)
                .orElseThrow(() -> new AssertionError("Missing annotation"));

        assertEquals(15, found.value());
    }

    @Test void missingFromTwoLevelMeta() {
        assertNull(
            new DirectMetaPresence(TwoLevelMeta.class)
                .find(B.class)
                .orElse(null));
    }

    @A(14) private static final class DirectlyOnType {
    }

    @D private static final class OneLevelMeta {
    }

    @G private static final class TwoLevelMeta {
    }
}
