package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotated.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.dulynoted.Presences.*;
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
        atomAnnotationOfValue(2),
        iotaAnnotationOfValue(3)));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Atom.class, target),
      containsInAnyOrder(atomOfValue(2)));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Iota.class, target),
      containsInAnyOrder(iotaOfValue(3)));
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
        atomAnnotationOfValue(2),
        iotaAnnotationOfValue(3)));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Atom.class, target),
      containsInAnyOrder(atomOfValue(2)));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Iota.class, target),
      containsInAnyOrder(iotaOfValue(3)));
  }
}
