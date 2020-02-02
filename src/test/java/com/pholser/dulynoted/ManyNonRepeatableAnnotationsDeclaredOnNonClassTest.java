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
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ManyNonRepeatableAnnotationsDeclaredOnNonClassTest {
    private AnnotatedElement target;

    @BeforeEach void setUp() throws Exception {
        target = X.class.getDeclaredMethod("foo");
    }

    @Test void findOneKindDirect() {
        Atom a =
            new DirectPresence().find(Atom.class, target)
                .orElseThrow(failure("Missing Atom annotation"));

        assertEquals(2, a.value());
    }

    @Test void findAnotherKindDirect() {
        Iota i =
            new DirectPresence().find(Iota.class, target)
                .orElseThrow(failure("Missing Iota annotation"));

        assertEquals(3, i.value());
    }

    @Test void allDirect() {
        List<Annotation> all = new DirectPresence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                atomAnnotationOfValue(2),
                iotaAnnotationOfValue(3)));
    }

    @Test void findAllOneKindDirectOrIndirect() {
        List<Atom> all =
            new DirectOrIndirectPresence().findAll(Atom.class, target);

        assertThat(all, containsInAnyOrder(atomOfValue(2)));
    }

    @Test void findAllAnotherKindDirectOrIndirect() {
        List<Iota> all =
            new DirectOrIndirectPresence().findAll(Iota.class, target);

        assertThat(all, containsInAnyOrder(iotaOfValue(3)));
    }

    @Test void findOneKindPresent() {
        Atom a =
            new Presence().find(Atom.class, target)
                .orElseThrow(failure("Missing Atom annotation"));

        assertEquals(2, a.value());
    }

    @Test void findAnotherKindPresent() {
        Iota i =
            new Presence().find(Iota.class, target)
                .orElseThrow(failure("Missing Iota annotation"));

        assertEquals(3, i.value());
    }

    @Test void allPresent() {
        List<Annotation> all = new Presence().all(target);

        assertThat(
            all,
            containsInAnyOrder(
                atomAnnotationOfValue(2),
                iotaAnnotationOfValue(3)));
    }

    @Test void findAllOneKindAssociated() {
        List<Atom> all =
            new AssociatedPresence().findAll(Atom.class, target);

        assertThat(all, containsInAnyOrder(atomOfValue(2)));
    }

    @Test void findAllAnotherKindAssociated() {
        List<Iota> all =
            new AssociatedPresence().findAll(Iota.class, target);

        assertThat(all, containsInAnyOrder(iotaOfValue(3)));
    }
}
