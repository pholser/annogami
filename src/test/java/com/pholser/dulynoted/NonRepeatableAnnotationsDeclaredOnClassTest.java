package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.AssertionHelp.*;
import static com.pholser.dulynoted.annotations.AnnotationMatching.*;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class NonRepeatableAnnotationsDeclaredOnClassTest {
    private AnnotatedElement target;

    @BeforeEach void setUp() {
        target = X.class;
    }

    @Test void findOneKindDirect() {
        Atom a =
            new DirectPresence().find(Atom.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(9, a.value());
    }

    @Test void findAnotherKindDirect() {
        new DirectPresence().find(Iota.class, target)
            .ifPresent(i -> fail("Iota should not be directly present here"));
    }

    @Test void allDirect() {
        List<Annotation> all = new DirectPresence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                atomAnnotationOfValue(9)));
    }

    @Test void findAllOneKindDirectOrIndirect() {
        List<Atom> all =
            new DirectOrIndirectPresence().findAll(Atom.class, target);

        assertThat(all, containsInAnyOrder(atomOfValue(9)));
    }

    @Test void findAllAnotherKindDirectOrIndirect() {
        List<Iota> all =
            new DirectOrIndirectPresence().findAll(Iota.class, target);

        assertEquals(emptyList(), all);
    }

    @Test void allDirectOrIndirect() {
        List<Annotation> all = new DirectOrIndirectPresence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                atomAnnotationOfValue(9)));
    }

    @Test void findOneKindPresent() {
        Atom a =
            new Presence().find(Atom.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(9, a.value());
    }

    @Test void findAnotherKindPresent() {
        Iota i =
            new Presence().find(Iota.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(10, i.value());
    }

    @Test void allPresent() {
        List<Annotation> all = new Presence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                atomAnnotationOfValue(9),
                iotaAnnotationOfValue(10)));
    }

    @Test void findAllOneKindAssociated() {
        List<Atom> all =
            new AssociatedPresence().findAll(Atom.class, target);

        assertThat(all, containsInAnyOrder(atomOfValue(9)));
    }

    @Test void findAllAnotherKindAssociated() {
        List<Iota> all =
            new AssociatedPresence().findAll(Iota.class, target);

        assertThat(all, containsInAnyOrder(iotaOfValue(10)));
    }

    @Test void allAssociated() {
        List<Annotation> all = new AssociatedPresence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                atomAnnotationOfValue(9),
                iotaAnnotationOfValue(10)));
    }
}
