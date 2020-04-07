package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.dulynoted.Presences.*;
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
        atomAnnotationOfValue(9)));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Atom.class, target),
      containsInAnyOrder(atomOfValue(9)));
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
        atomAnnotationOfValue(9),
        iotaAnnotationOfValue(10)));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Atom.class, target),
      containsInAnyOrder(atomOfValue(9)));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Iota.class, target),
      containsInAnyOrder(iotaOfValue(10)));
  }
}
