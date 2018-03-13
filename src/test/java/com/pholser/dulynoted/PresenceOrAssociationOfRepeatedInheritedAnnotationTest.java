package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Marker;
import com.pholser.dulynoted.annotations.SuperAggregate;
import com.pholser.dulynoted.annotations.SuperUnit;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.annotations.Units.*;
import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class PresenceOrAssociationOfRepeatedInheritedAnnotationTest {
    private AnnotatedElement target;

    @BeforeEach void setup() {
        target = X.class;
    }

    @Test void askingForDeclaredSingle() {
        assertNull(target.getDeclaredAnnotation(SuperUnit.class));
    }

    @Test void askingForDeclaredMulti() {
        SuperUnit[] units = target.getDeclaredAnnotationsByType(SuperUnit.class);

        assertEquals(0, units.length);
    }

    @Test void askingForDeclaredAll() {
        List<Annotation> annotations = asList(target.getDeclaredAnnotations());

        assertEquals(1, annotations.size());

        Aggregate aggregate = (Aggregate) annotations.get(0);
        assertEquals(2, aggregate.value().length);
        List<Unit> units = asList(aggregate.value());
        assertThat(
            units,
            containsInAnyOrder(unitOfValue(1), unitOfValue(2)));
    }

    @Test void askingForSingle() {
        assertNull(target.getAnnotation(SuperUnit.class));
    }

    @Test void askingForMulti() {
        List<SuperUnit> units =
            asList(target.getAnnotationsByType(SuperUnit.class));

        assertEquals(2, units.size());
        assertThat(
            units,
            containsInAnyOrder(superUnitOfValue(7), superUnitOfValue(8)));
    }

    @Test void askingForAll() {
        List<Annotation> units = asList(target.getAnnotations());

        assertEquals(3, units.size());
        assertThat(
            units,
            containsInAnyOrder(
                instanceOf(SuperAggregate.class),
                instanceOf(Aggregate.class),
                instanceOf(Marker.class)));
    }
}
