package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Particle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotatedPathFromClassToDeclaringPackageTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() {
    path =
      AnnotatedPath.fromClass(AnnotationsGalore.class)
        .toDeclaringPackage()
        .build();
  }

  @Test void findFirstOnPackageOfClass() {
    Particle p =
      path.findFirst(Particle.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(8, p.value());
  }
}
