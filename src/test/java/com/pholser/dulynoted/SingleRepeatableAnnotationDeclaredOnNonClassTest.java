package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.AssertionHelp.*;
import static com.pholser.dulynoted.annotations.AnnotationMatching.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class SingleRepeatableAnnotationDeclaredOnNonClassTest {
    private AnnotatedElement target;

    @BeforeEach void setUp() throws Exception {
        target = X.class.getDeclaredField("s");
    }

    @Test void findDirect() {
        Particle p =
            new DirectPresence().find(Particle.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(4, p.value());
    }

    @Test void allDirect() {
        List<Annotation> all = new DirectPresence().all(target);

        assertThat(all, containsInAnyOrder(particleAnnotationOfValue(4)));
    }

    @Test void findAllDirectOrIndirect() {
        List<Particle> all =
            new DirectOrIndirectPresence().findAll(Particle.class, target);

        assertThat(all, containsInAnyOrder(particleOfValue(4)));
    }

    @Test void findPresent() {
        Particle p =
            new Presence().find(Particle.class, target)
                .orElseThrow(failure("Missing annotation"));

        assertEquals(4, p.value());
    }

    @Test void allPresent() {
        List<Annotation> all = new Presence().all(target);

        assertThat(all, containsInAnyOrder(particleAnnotationOfValue(4)));
    }

    @Test void findAllAssociated() {
        List<Particle> all =
            new AssociatedPresence().findAll(Particle.class, target);

        assertThat(all, containsInAnyOrder(particleOfValue(4)));
    }
}
