package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotationsGalore;
import com.pholser.dulynoted.annotations.Iota;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.dulynoted.Presences.ASSOCIATED;
import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.dulynoted.Presences.PRESENT;
import static com.pholser.dulynoted.annotations.Annotations.annoValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromClassToClassHierarchyTest {
  @Test void findFirstDirectFromClassThruDepthHierarchy() {
    AnnotatedPath path =
      AnnotatedPath.fromClass(AnnotationsGalore.class)
        .toDepthHierarchy()
        .build();

    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(3, i.value());
  }

  @Test void findAllByTypeDirectIndirectFromClassThruDepthHierarchy() {
    AnnotatedPath path =
      AnnotatedPath.fromClass(AnnotationsGalore.class)
        .toDepthHierarchy()
        .build();

    List<Iota> iotas = path.findAll(Iota.class, DIRECT_OR_INDIRECT);

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
      AnnotatedPath.fromClass(AnnotationsGalore.class)
        .toBreadthHierarchy()
        .build();

    List<Iota> iotas = path.findAll(Iota.class, DIRECT_OR_INDIRECT);

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
      AnnotatedPath.fromClass(Object.class)
        .toClassEnclosure()
        .build();

    path.findFirst(Documented.class, PRESENT)
      .ifPresent(d -> fail("Should not have found " + d));
  }

  @Test void findAllOnEmptyHierarchy() {
    AnnotatedPath path =
      AnnotatedPath.fromClass(Object.class)
        .toClassEnclosure()
        .build();

    List<Retention> retentions =
      path.findAll(Retention.class, ASSOCIATED);

    assertEquals(List.of(), retentions);
  }
}
