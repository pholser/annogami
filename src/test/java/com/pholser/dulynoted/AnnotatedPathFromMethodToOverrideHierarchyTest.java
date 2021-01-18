package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotationsGalore;
import com.pholser.dulynoted.annotations.Atom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.dulynoted.annotations.Annotations.annoValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromMethodToOverrideHierarchyTest {
  @Test void findFirstDepthFirstDirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromMethod(
        AnnotationsGalore.class.getDeclaredMethod(
          "self",
          int.class,
          Object.class))
        .toDepthOverridden()
        .build();

    Atom a =
      path.findFirst(Atom.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(20, a.value());
  }

  @Test void findAllDepthFirstDirectOrIndirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromMethod(
        AnnotationsGalore.class.getDeclaredMethod(
          "self",
          int.class,
          Object.class))
        .toDepthOverridden()
        .build();

    List<Atom> atoms =
      path.findAll(Atom.class, DIRECT_OR_INDIRECT);

    assertEquals(
      List.of(
        annoValue(Atom.class, 20),
        annoValue(Atom.class, 21),
        annoValue(Atom.class, 22),
        annoValue(Atom.class, 23),
        annoValue(Atom.class, 24),
        annoValue(Atom.class, 25),
        annoValue(Atom.class, 26),
        annoValue(Atom.class, 27),
        annoValue(Atom.class, 28)),
      atoms);
  }

  @Test void findAllBreadthFirstDirectOrIndirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromMethod(
        AnnotationsGalore.class.getDeclaredMethod(
          "self",
          int.class,
          Object.class))
        .toBreadthOverridden()
        .build();

    List<Atom> atoms =
      path.findAll(Atom.class, DIRECT_OR_INDIRECT);

    assertEquals(
      List.of(
        annoValue(Atom.class, 20),
        annoValue(Atom.class, 21),
        annoValue(Atom.class, 25),
        annoValue(Atom.class, 23),
        annoValue(Atom.class, 22),
        annoValue(Atom.class, 27),
        annoValue(Atom.class, 26),
        annoValue(Atom.class, 24),
        annoValue(Atom.class, 28)),
      atoms);
  }
}
