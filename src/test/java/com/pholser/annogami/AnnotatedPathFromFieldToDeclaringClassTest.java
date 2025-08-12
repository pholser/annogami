package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
