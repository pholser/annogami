package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.X;
import com.pholser.dulynoted.annotations.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.dulynoted.Presences.ASSOCIATED;
import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.dulynoted.Presences.PRESENT;
import static com.pholser.dulynoted.annotations.Annotations.annoValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
      containsInAnyOrder(annoValue(Particle.class, 4)));
  }

  @Test void findAllDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Particle.class, target),
      containsInAnyOrder(annoValue(Particle.class, 4)));
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
      containsInAnyOrder(annoValue(Particle.class, 4)));
  }

  @Test void findAllAssociated() {
    assertThat(
      ASSOCIATED.findAll(Particle.class, target),
      containsInAnyOrder(annoValue(Particle.class, 4)));
  }
}
