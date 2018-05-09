package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;

import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Compound;
import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.Y;
import org.junit.jupiter.api.BeforeEach;
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

    @Test void findAllOneKindDirect() {
        List<Particle> all =
            new DirectPresence().findAll(Particle.class, target);

        assertEquals(emptyList(), all);
    }

    @Test void findAllOneContainerKindDirect() {
        List<Compound> all =
            new DirectPresence().findAll(Compound.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                compoundWith(
                    particleOfValue(-1),
                    particleOfValue(-2))));
    }

    @Test void findAllAnotherKindDirect() {
        List<Unit> all =
            new DirectPresence().findAll(Unit.class, target);

        assertThat(
            all,
            containsInAnyOrder(unitOfValue(-3)));
    }

    @Test void findAllAnotherContainerKindDirect() {
        List<Aggregate> all =
            new DirectPresence().findAll(Aggregate.class, target);

        assertEquals(emptyList(), all);
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

    @Test void findOneKindDirectOrIndirect() {
        new DirectOrIndirectPresence().find(Particle.class, target)
            .ifPresent(p -> fail("Single Particle should not be found"));
    }

    @Test void findOneContainerKindDirectOrIndirect() {
        Compound c =
            new DirectOrIndirectPresence().find(Compound.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertThat(
            asList(c.value()),
            containsInAnyOrder(
                particleOfValue(-1),
                particleOfValue(-2)));
    }

    @Test void findAnotherKindDirectOrIndirect() {
        Unit u =
            new DirectOrIndirectPresence().find(Unit.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(-3, u.value());
    }

    @Test void findAnotherContainerKindDirectOrIndirect() {
        new DirectOrIndirectPresence().find(Aggregate.class, target)
            .ifPresent(p ->
                fail("Aggregate annotation should not be found"));
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

    @Test void findAllOneKindPresent() {
        List<Particle> all = new Presence().findAll(Particle.class, target);

        assertThat(
            all,
            containsInAnyOrder(particleOfValue(-6)));
    }

    @Test void findAllOneContainerKindPresent() {
        List<Compound> all = new Presence().findAll(Compound.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                compoundWith(
                    particleOfValue(-1),
                    particleOfValue(-2))));
    }

    @Test void findAllAnotherKindPresent() {
        List<Unit> all = new Presence().findAll(Unit.class, target);

        assertThat(
            all,
            containsInAnyOrder(unitOfValue(-3)));
    }

    @Test void findAllAnotherContainerKindPresent() {
        List<Aggregate> all =
            new Presence().findAll(Aggregate.class, target);

        assertThat(
            all,
            containsInAnyOrder(
                aggregateWith(
                    unitOfValue(-4),
                    unitOfValue(-5))));
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
                particleAnnotationOfValue(-6)));
    }

    @Test void findOneKindAssociated() {
    }

    @Test void findOneContainerKindAssociated() {
    }

    @Test void findAnotherKindAssociated() {
    }

    @Test void findAnotherContainerKindAssociated() {
    }

    @Test void findAllOneKindAssociated() {
    }

    @Test void findAllOneContainerKindAssociated() {
    }

    @Test void findAllAnotherKindAssociated() {
    }

    @Test void findAllAnotherContainerKindAssociated() {
    }

    @Test void allAssociated() {
    }
}
