package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotatedPathFromParameterToDeclaringMethodTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    path =
      AnnotatedPath.fromParameter(
        AnnotationsGalore.class
          .getMethod("foo", int.class)
          .getParameters()[0])
        .toDeclaringMethod()
        .build();
  }

  @Test void findFirstOnParameter() {
    Atom a =
      path.findFirst(Atom.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(4, a.value());
  }

  @Test void findFirstOnMethodOfParameter() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(5, i.value());
  }
}
