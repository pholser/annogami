package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotationsGalore;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;

import static com.pholser.dulynoted.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromParameterToDeclaringCtorTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    Parameter p =
      AnnotationsGalore.class
        .getConstructor(int.class)
        .getParameters()[0];
    path =
      AnnotatedPath.fromParameter(p)
        .toDeclaringConstructor()
        .build();
  }

  @Test void findFirstOnParameter() {
    Atom a =
      path.findFirst(Atom.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(1, a.value());
  }

  @Test void findFirstOnCtorOfParameter() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(2, i.value());
  }
}
