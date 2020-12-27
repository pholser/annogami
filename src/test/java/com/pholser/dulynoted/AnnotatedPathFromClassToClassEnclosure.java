package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.ClassEnclosure.Enclosed1;
import com.pholser.dulynoted.annotated.ClassEnclosure.Enclosed1.Enclosed2;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.Unit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.dulynoted.annotations.Annotations.annoValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AnnotatedPathFromClassToClassEnclosure {
  @Test void findFirstDirectEnclosed2() {
    AnnotatedPath path =
      AnnotatedPath.fromClass(Enclosed2.class)
        .toClassEnclosure()
        .build();

    Unit u =
      path.findFirst(Unit.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(-7, u.value());
  }

  @Test void findFirstDirectEnclosed1A() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromClass(
        Class.forName(Enclosed1.class.getName() + "$1A"))
        .toClassEnclosure()
        .build();

    Unit u =
      path.findFirst(Unit.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(-1, u.value());
  }

  @Test void findFirstDirectOnEnclosureOfEnclosed1A() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromClass(
        Class.forName(Enclosed1.class.getName() + "$1A"))
        .toClassEnclosure()
        .build();

    Iota i =
      path.findFirst(Iota.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(-3, i.value());
  }

  @Test void findAllDirectOrIndirectEnclosed2B() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromClass(
        Class.forName(Enclosed2.class.getName() + "$1B"))
        .toClassEnclosure()
        .build();

    List<Unit> units =
      path.findAll(Unit.class, DIRECT_OR_INDIRECT);

    assertEquals(
      List.of(
        annoValue(Unit.class, -5),
        annoValue(Unit.class, -7),
        annoValue(Unit.class, -3),
        annoValue(Unit.class, -4),
        annoValue(Unit.class, -9)),
      units);
  }
}
