package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;

import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.Presences.*;
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
      DIRECT.find(Particle.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(4, p.value());
  }

  @Test void allDirect() {
    assertThat(
      DIRECT.all(target),
      containsInAnyOrder(particleAnnotationOfValue(4)));
  }

  @Test void findAllDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Particle.class, target),
      containsInAnyOrder(particleOfValue(4)));
  }

  @Test void findPresent() {
    Particle p =
      PRESENT.find(Particle.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(4, p.value());
  }

  @Test void allPresent() {
    assertThat(
      PRESENT.all(target),
      containsInAnyOrder(particleAnnotationOfValue(4)));
  }

  @Test void findAllAssociated() {
    assertThat(
      ASSOCIATED.findAll(Particle.class, target),
      containsInAnyOrder(particleOfValue(4)));
  }
}
