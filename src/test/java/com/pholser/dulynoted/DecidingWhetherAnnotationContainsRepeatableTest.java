package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.Annotations.*;
import static org.junit.jupiter.api.Assertions.*;

class DecidingWhetherAnnotationContainsRepeatableTest {
    @Test void containerOfRepeatableAnnotation() throws Exception {
        assertTrue(
            containsRepeatableAnnotation(
                X.class.getDeclaredMethod("foo")
                    .getDeclaredAnnotation(Aggregate.class)));
    }

    @Test void notAContainerOfRepeatableAnnotation() {
        assertFalse(
            containsRepeatableAnnotation(
                X.class.getDeclaredAnnotationsByType(Unit.class)[0]));
    }
}
