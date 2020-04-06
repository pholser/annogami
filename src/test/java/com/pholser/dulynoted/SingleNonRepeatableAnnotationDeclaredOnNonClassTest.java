package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.AssertionHelp.*;
import static com.pholser.dulynoted.Presences.*;
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
      DIRECT.find(Atom.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertEquals(1, a.value());
  }

  @Test void allDirect() {
    assertThat(
      DIRECT.all(target),
      containsInAnyOrder(atomAnnotationOfValue(1)));
  }

  @Test void findAllDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Atom.class, target),
      containsInAnyOrder(atomOfValue(1)));
  }

  @Test void findPresent() {
    Atom a =
      PRESENT.find(Atom.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertEquals(1, a.value());
  }

  @Test void allPresent() {
    assertThat(
      PRESENT.all(target),
      containsInAnyOrder(atomAnnotationOfValue(1)));
  }

  @Test void findAllAssociated() {
    assertThat(
      ASSOCIATED.findAll(Atom.class, target),
      containsInAnyOrder(atomOfValue(1)));
  }
}
