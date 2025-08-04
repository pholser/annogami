package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Iota;
import com.pholser.annogami.annotations.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromCtorToDeclaringClassTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    Constructor<?> c =
      AnnotationsGalore.class.getConstructor(int.class);
    path =
      AnnotatedPath.fromConstructor(c)
        .toDeclaringClass()
        .build();
  }

  @Test void findFirstOnCtor() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(2, i.value());
  }

  @Test void findFirstOnDeclaringClassOfCtor() {
    Unit u =
      path.findFirst(Unit.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(6, u.value());
  }
}
