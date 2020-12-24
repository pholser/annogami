package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotationsGalore;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromLocalClassToEnclosingConstructorTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    path =
      AnnotatedPath.fromClass(
        Class.forName(AnnotationsGalore.class.getName() + "$1Local"))
        .toEnclosingConstructor()
        .build();
  }

  @Test void findFirstOnLocalClass() {
    Atom a =
      path.findFirst(Atom.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(2, a.value());
  }

  @Test void findFirstOnEnclosingCtorOfLocalClass() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(9, i.value());
  }

  @Test void notALocalClass() {
    assertThrows(
      IllegalStateException.class,
      () ->
        AnnotatedPath.fromClass(AnnotationsGalore.class)
          .toEnclosingConstructor());
  }
}
