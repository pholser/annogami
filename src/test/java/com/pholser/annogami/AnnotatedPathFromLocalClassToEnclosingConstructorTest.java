package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        .orElseGet(Assertions::fail);

    assertEquals(2, a.value());
  }

  @Test void findFirstOnEnclosingCtorOfLocalClass() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(Assertions::fail);

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
