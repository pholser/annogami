package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Marker;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class IndirectPresenceOfNonRepeatedRepeatableAnnotation {
    private AnnotatedElement target;

    @BeforeEach void setup() throws Exception {
        target = X.class.getDeclaredMethod("bar");
    }

    @Test void askingForDeclaredSingle() {
        assertNull(target.getDeclaredAnnotation(Unit.class));
    }

    @Test void askingForDeclaredMulti() {
        Unit[] units = target.getDeclaredAnnotationsByType(Unit.class);

        assertEquals(1, units.length);
        assertEquals(6, units[0].value());
    }

    @Test void askingForDeclaredAll() {
        Annotation[] annotations = target.getDeclaredAnnotations();

        assertEquals(1, annotations.length);

        Aggregate aggregate = (Aggregate) annotations[0];
        Unit[] units = aggregate.value();
        assertEquals(1, units.length);
        assertEquals(6, units[0].value());
    }

    @Test void askingForSingle() {
        assertNull(target.getAnnotation(Unit.class));
    }

    @Test void askingForMulti() {
        Unit[] units = target.getDeclaredAnnotationsByType(Unit.class);

        assertEquals(1, units.length);
        assertEquals(6, units[0].value());
    }

    @Test void askingForAll() {
        Annotation[] annotations = target.getAnnotations();

        assertEquals(1, annotations.length);

        Aggregate aggregate = (Aggregate) annotations[0];
        Unit[] units = aggregate.value();
        assertEquals(1, units.length);
        assertEquals(6, units[0].value());
    }
}
