package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Iota;
import com.pholser.annogami.annotations.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotatedPathFromCtorToDeclaringClassTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    path =
      AnnotatedPathBuilder.fromConstructor(
        AnnotationsGalore.class.getConstructor(int.class))
        .toDeclaringClass()
        .build();
  }

  @Test void findFirstOnCtor() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(2, i.value());
  }

  @Test void findFirstOnDeclaringClassOfCtor() {
    Unit u =
      path.findFirst(Unit.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(6, u.value());
  }
}
