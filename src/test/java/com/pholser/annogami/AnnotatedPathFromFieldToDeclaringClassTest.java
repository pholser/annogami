package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotatedPathFromFieldToDeclaringClassTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    path =
      AnnotatedPath.fromField(
        AnnotationsGalore.class.getDeclaredField("i"))
        .toDeclaringClass()
        .build();
  }

  @Test void findFirstOnField() {
    Atom a =
      path.findFirst(Atom.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(7, a.value());
  }

  @Test void findFirstOnDeclaringClassOfField() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(3, i.value());
  }
}
