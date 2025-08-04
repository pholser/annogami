package com.pholser.annogami;

import com.pholser.annogami.annotated.X;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.annogami.Presences.ASSOCIATED;
import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.PRESENT;
import static com.pholser.annogami.annotations.Annotations.annoValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ManyNonRepeatableAnnotationsDeclaredOnNonClassTest {
  private AnnotatedElement target;

  @BeforeEach void setUp() throws Exception {
    target = X.class.getDeclaredMethod("foo");
  }

  @Test void findOneKindDirect() {
    Atom a =
      DIRECT.find(Atom.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(2, a.value());
  }

  @Test void findAnotherKindDirect() {
    Iota i =
      DIRECT.find(Iota.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(3, i.value());
  }

  @Test void allDirect() {
    assertThat(
      DIRECT.all(target),
      containsInAnyOrder(
        annoValue(Atom.class, 2),
        annoValue(Iota.class, 3)));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Atom.class, target),
      containsInAnyOrder(annoValue(Atom.class, 2)));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Iota.class, target),
      containsInAnyOrder(annoValue(Iota.class, 3)));
  }

  @Test void findOneKindPresent() {
    Atom a =
      PRESENT.find(Atom.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(2, a.value());
  }

  @Test void findAnotherKindPresent() {
    Iota i =
      PRESENT.find(Iota.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(3, i.value());
  }

  @Test void allPresent() {
    assertThat(
      PRESENT.all(target),
      containsInAnyOrder(
        annoValue(Atom.class, 2),
        annoValue(Iota.class, 3)));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Atom.class, target),
      containsInAnyOrder(annoValue(Atom.class, 2)));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Iota.class, target),
      containsInAnyOrder(annoValue(Iota.class, 3)));
  }
}
