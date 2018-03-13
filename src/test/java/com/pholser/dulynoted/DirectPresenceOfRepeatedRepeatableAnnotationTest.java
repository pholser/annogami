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

import static com.pholser.dulynoted.annotations.Units.*;
import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class DirectPresenceOfRepeatedRepeatableAnnotationTest {
    private AnnotatedElement target;

    @BeforeEach void setup() {
        target = X.class;
    }

    @Test void askingForDeclaredSingle() {
        assertNull(target.getDeclaredAnnotation(Unit.class));
    }

    @Test void askingForDeclaredMulti() {
        List<Unit> units =
            asList(target.getDeclaredAnnotationsByType(Unit.class));

        assertEquals(2, units.size());
        assertThat(
            units,
            containsInAnyOrder(unitOfValue(1), unitOfValue(2)));
    }

    @Test void askingForDeclaredAll() {
        Annotation[] annotations = target.getDeclaredAnnotations();

        assertEquals(1, annotations.length);

        Aggregate aggregate = (Aggregate) annotations[0];
        List<Unit> units = asList(aggregate.value());

        assertThat(
            units,
            containsInAnyOrder(
                unitAnnotationOfValue(1),
                unitAnnotationOfValue(2)));
    }

    @Test void askingForSingle() {
        assertNull(target.getAnnotation(Unit.class));
    }

    @Test void askingForMulti() {
        List<Unit> units = asList(target.getAnnotationsByType(Unit.class));

        assertEquals(2, units.size());
        assertThat(
            units,
            containsInAnyOrder(unitOfValue(1), unitOfValue(2)));
    }

    @Test void askingForAll() {
        Annotation[] annotations = target.getAnnotations();

        assertEquals(3, annotations.length);

        Aggregate[] aggregates =
            target.getDeclaredAnnotationsByType(Aggregate.class);
        assertEquals(1, aggregates.length);
        List<Unit> units = asList(aggregates[0].value());

        assertThat(
            units,
            containsInAnyOrder(
                unitAnnotationOfValue(1),
                unitAnnotationOfValue(2)));
    }
}
