package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotationsGalore;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.pholser.dulynoted.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromMethodToDeclaringClassTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    Method m = AnnotationsGalore.class.getMethod("foo", int.class);
    path =
      AnnotatedPath.fromMethod(m)
        .toDeclaringClass()
        .build();
  }

  @Test void findFirstOnMethod() {
    Atom a =
      path.findFirst(Atom.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(5, a.value());
  }

  @Test void findFirstOnDeclaringClassOfMethod() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(5, i.value());
  }
}
