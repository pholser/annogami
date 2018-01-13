package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Marker;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class DirectPresenceOfNonRepeatedRepeatableAnnotation {
    private AnnotatedElement target;

    @BeforeEach void setup() throws Exception {
        target = X.class.getDeclaredField("i");
    }

    @Test void askingForDeclaredSingle() {
        assertEquals(3, target.getDeclaredAnnotation(Unit.class).value());
    }

    @Test void askingForDeclaredMulti() {
        Unit[] units = target.getDeclaredAnnotationsByType(Unit.class);

        assertEquals(1, units.length);
        assertEquals(3, units[0].value());
    }

    @Test void askingForDeclaredAll() {
        List<Annotation> units = asList(target.getDeclaredAnnotations());

        assertEquals(2, units.size());
        assertThat(
            units,
            containsInAnyOrder(instanceOf(Unit.class), instanceOf(Marker.class)));
    }

    @Test void askingForSingle() {
        assertEquals(3, target.getAnnotation(Unit.class).value());
    }

    @Test void askingForMulti() {
        Unit[] units = target.getAnnotationsByType(Unit.class);

        assertEquals(1, units.length);
        assertEquals(3, units[0].value());
    }

    @Test void askingForAll() {
        List<Annotation> units = asList(target.getAnnotations());

        assertEquals(2, units.size());
        assertThat(
            units,
            containsInAnyOrder(instanceOf(Unit.class), instanceOf(Marker.class)));
    }
}
