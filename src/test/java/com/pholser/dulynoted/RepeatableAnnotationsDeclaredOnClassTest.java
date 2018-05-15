package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Compound;
import com.pholser.dulynoted.annotations.Many;
import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.Single;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.Y;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.AssertionHelp.*;
import static com.pholser.dulynoted.annotations.AnnotationMatching.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class RepeatableAnnotationsDeclaredOnClassTest {
    private AnnotatedElement target;

    @BeforeEach void setUp() {
        target = Y.class;
    }

    @Test void findOneKindDirect() {
        new DirectPresence().find(Particle.class, target)
            .ifPresent(p -> fail("Single Particle should not be found"));
    }

    @Test void findOneContainerKindDirect() {
        Compound c =
            new DirectPresence().find(Compound.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertThat(
            asList(c.value()),
            containsInAnyOrder(
                particleOfValue(-1),
                particleOfValue(-2)));
    }

    @Test void findAnotherKindDirect() {
        Unit u =
            new DirectPresence().find(Unit.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(-3, u.value());
    }

    @Test void findAnotherContainerKindDirect() {
        new DirectPresence().find(Aggregate.class, target)
            .ifPresent(p ->
                fail("Aggregate annotation should not be found"));
    }

    @Test void allDirect() {
        List<Annotation> all = new DirectPresence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                compoundAnnotationWith(
                    particleOfValue(-1),
                    particleOfValue(-2)),
                unitAnnotationOfValue(-3)));
    }

    @Test void findAllOneKindDirectOrIndirect() {
        List<Particle> all =
            new DirectOrIndirectPresence().findAll(Particle.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                particleOfValue(-1),
                particleOfValue(-2)));
    }

    @Test void findAllOneContainerKindDirectOrIndirect() {
        List<Compound> all =
            new DirectOrIndirectPresence().findAll(Compound.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                compoundWith(
                    particleOfValue(-1),
                    particleOfValue(-2))));
    }

    @Test void findAllAnotherKindDirectOrIndirect() {
        List<Unit> all =
            new DirectOrIndirectPresence().findAll(Unit.class, target);

        assertThat(
            all,
            containsInAnyOrder(unitOfValue(-3)));
    }

    @Test void findAllAnotherContainerKindDirectOrIndirect() {
        List<Aggregate> all =
            new DirectOrIndirectPresence().findAll(Aggregate.class, target);

        assertEquals(emptyList(), all);
    }

    @Test void allDirectOrIndirect() {
        List<Annotation> all = new DirectOrIndirectPresence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                compoundAnnotationWith(
                    particleOfValue(-1),
                    particleOfValue(-2)),
                unitAnnotationOfValue(-3),
                particleAnnotationOfValue(-1),
                particleAnnotationOfValue(-2)));
    }

    @Test void findOneKindPresent() {
        Particle p =
            new Presence().find(Particle.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(-6, p.value());
    }

    @Test void findOneContainerKindPresent() {
        Compound c =
            new Presence().find(Compound.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertThat(
            asList(c.value()),
            containsInAnyOrder(
                particleOfValue(-1),
                particleOfValue(-2)));
    }

    @Test void findAnotherKindPresent() {
        Unit u =
            new Presence().find(Unit.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(-3, u.value());
    }

    @Test void findAnotherContainerKindPresent() {
        Aggregate a =
            new Presence().find(Aggregate.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertThat(
            asList(a.value()),
            containsInAnyOrder(
                unitOfValue(-4),
                unitOfValue(-5)));
    }

    @Test void allPresent() {
        List<Annotation> all = new Presence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                compoundAnnotationWith(
                    particleOfValue(-1),
                    particleOfValue(-2)),
                unitAnnotationOfValue(-3),
                aggregateAnnotationWith(
                    unitOfValue(-4),
                    unitOfValue(-5)),
                particleAnnotationOfValue(-6),
                manyAnnotationWith(
                    singleOfValue(-7),
                    singleOfValue(-8)
                )));
    }

    @Test void findAllOneKindAssociated() {
        List<Particle> all =
            new AssociatedPresence().findAll(Particle.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                particleOfValue(-1),
                particleOfValue(-2)));
    }

    @Test void findAllOneContainerKindAssociated() {
        List<Compound> all =
            new AssociatedPresence().findAll(Compound.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                compoundWith(
                    particleOfValue(-1),
                    particleOfValue(-2))));
    }

    @Test void findAllAnotherKindAssociated() {
        List<Single> all =
            new AssociatedPresence().findAll(Single.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                singleOfValue(-7),
                singleOfValue(-8)));
    }

    @Test void findAllAnotherContainerKindAssociated() {
        List<Many> all =
            new AssociatedPresence().findAll(Many.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                manyWith(
                    singleOfValue(-7),
                    singleOfValue(-8))));
    }

    @Test void allAssociated() {
        List<Annotation> all = new AssociatedPresence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                particleAnnotationOfValue(-1),
                particleAnnotationOfValue(-2),
                compoundAnnotationWith(
                    particleOfValue(-1),
                    particleOfValue(-2)),
                unitAnnotationOfValue(-3),
                aggregateAnnotationWith(
                    unitOfValue(-4),
                    unitOfValue(-5)),
                singleAnnotationOfValue(-7),
                singleAnnotationOfValue(-8),
                manyAnnotationWith(
                    singleOfValue(-7),
                    singleOfValue(-8))));
    }
}
