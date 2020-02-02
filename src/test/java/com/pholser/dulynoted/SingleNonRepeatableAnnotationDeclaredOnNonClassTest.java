package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.AssertionHelp.*;
import static com.pholser.dulynoted.annotations.AnnotationMatching.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class SingleNonRepeatableAnnotationDeclaredOnNonClassTest {
    private AnnotatedElement target;

    @BeforeEach void setUp() throws Exception {
        target = X.class.getDeclaredField("i");
    }

    @Test void findDirect() {
        Atom a =
            new DirectPresence().find(Atom.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(1, a.value());
    }

    @Test void allDirect() {
        List<Annotation> all = new DirectPresence().all(target);

        assertThat(all, containsInAnyOrder(atomAnnotationOfValue(1)));
    }

    @Test void findAllDirectOrIndirect() {
        List<Atom> all =
            new DirectOrIndirectPresence().findAll(Atom.class, target);

        assertThat(all, containsInAnyOrder(atomOfValue(1)));
    }

    @Test void findPresent() {
        Atom a =
            new Presence().find(Atom.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(1, a.value());
    }

    @Test void allPresent() {
        List<Annotation> all = new Presence().all(target);

        assertThat(all, containsInAnyOrder(atomAnnotationOfValue(1)));
    }

    @Test void findAllAssociated() {
        List<Atom> all =
            new AssociatedPresence().findAll(Atom.class, target);

        assertThat(all, containsInAnyOrder(atomOfValue(1)));
    }
}
