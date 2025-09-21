package com.pholser.annogami;

import com.pholser.annogami.annotated.AnnotationsGalore;
import com.pholser.annogami.annotations.Iota;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.util.Collections;
import java.util.List;

import static com.pholser.annogami.Presences.ASSOCIATED;
import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.PRESENT;
import static com.pholser.annogami.annotations.Annotations.annoValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotatedPathFromClassToClassHierarchyTest {
  @Test void findFirstDirectFromClassThruDepthHierarchy() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(AnnotationsGalore.class)
        .toDepthHierarchy()
        .build();

    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(3, i.value());
  }

  @Test void findAllByTypeDirectIndirectFromClassThruDepthHierarchy() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(AnnotationsGalore.class)
        .toDepthHierarchy()
        .build();

    List<Iota> iotas = path.find(Iota.class, DIRECT_OR_INDIRECT);

    assertEquals(
      List.of(
        annoValue(Iota.class, 3),
        annoValue(Iota.class, -10),
        annoValue(Iota.class, -11),
        annoValue(Iota.class, -12),
        annoValue(Iota.class, -13),
        annoValue(Iota.class, -14),
        annoValue(Iota.class, -15),
        annoValue(Iota.class, -16),
        annoValue(Iota.class, -17)),
      iotas);
  }

  @Test void findAllByTypeDirectIndirectFromClassThruBreadthHierarchy() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(AnnotationsGalore.class)
        .toBreadthHierarchy()
        .build();

    List<Iota> iotas = path.find(Iota.class, DIRECT_OR_INDIRECT);

    assertEquals(
      List.of(
        annoValue(Iota.class, 3),
        annoValue(Iota.class, -10),
        annoValue(Iota.class, -14),
        annoValue(Iota.class, -12),
        annoValue(Iota.class, -11),
        annoValue(Iota.class, -16),
        annoValue(Iota.class, -15),
        annoValue(Iota.class, -13),
        annoValue(Iota.class, -17)),
      iotas);
  }

  @Test void findFirstOnEmptyHierarchy() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Object.class)
        .toClassEnclosure()
        .build();

    path.findFirst(Documented.class, PRESENT)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void findAllOnEmptyHierarchy() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Object.class)
        .toClassEnclosure()
        .build();

    List<Retention> retentions = path.find(Retention.class, ASSOCIATED);

    assertEquals(Collections.emptyList(), retentions);
  }
}
