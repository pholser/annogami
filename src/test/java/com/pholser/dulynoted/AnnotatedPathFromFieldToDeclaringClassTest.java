package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotationsGalore;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.pholser.dulynoted.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromFieldToDeclaringClassTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    Field f = AnnotationsGalore.class.getDeclaredField("i");
    path =
      AnnotatedPath.fromField(f)
        .toDeclaringClass()
        .build();
  }

  @Test void findFirstOnField() {
    Atom a =
      path.findFirst(Atom.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(7, a.value());
  }

  @Test void findFirstOnDeclaringClassOfField() {
    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(3, i.value());
  }
}
