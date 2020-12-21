package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.X;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.dulynoted.Presences.ASSOCIATED;
import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.dulynoted.Presences.PRESENT;
import static com.pholser.dulynoted.annotations.Annotations.annoValue;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class NonRepeatableAnnotationsDeclaredOnClassTest {
  private AnnotatedElement target;

  @BeforeEach void setUp() {
    target = X.class;
  }

  @Test void findOneKindDirect() {
    Atom a =
      DIRECT.find(Atom.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(9, a.value());
  }

  @Test void findAnotherKindDirect() {
    DIRECT.find(Iota.class, target)
      .ifPresent(i -> fail("Iota should not be directly present here"));
  }

  @Test void allDirect() {
    assertThat(
      DIRECT.all(target),
      containsInAnyOrder(
        annoValue(Atom.class, 9)));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Atom.class, target),
      containsInAnyOrder(
        annoValue(Atom.class, 9)));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertEquals(
      emptyList(),
      DIRECT_OR_INDIRECT.findAll(Iota.class, target));
  }

  @Test void findOneKindPresent() {
    Atom a =
      PRESENT.find(Atom.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(9, a.value());
  }

  @Test void findAnotherKindPresent() {
    Iota i =
      PRESENT.find(Iota.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(10, i.value());
  }

  @Test void allPresent() {
    assertThat(
      PRESENT.all(target),
      containsInAnyOrder(
        annoValue(Atom.class, 9),
        annoValue(Iota.class, 10)));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Atom.class, target),
      containsInAnyOrder(annoValue(Atom.class, 9)));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Iota.class, target),
      containsInAnyOrder(annoValue(Iota.class, 10)));
  }
}
