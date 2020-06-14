package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotatedParameterHaver;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pholser.dulynoted.Presences.*;
import static com.pholser.dulynoted.annotations.AnnotationMatching.unitOfValue;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class BuildingAnnotatedPathsWithOnlyAConstructorParameterTest {
  private AnnotatedPath path;

  @BeforeEach void setUp() throws Exception {
    path =
      AnnotatedPath.fromParameter(
        AnnotatedParameterHaver.class
          .getConstructor(int.class)
          .getParameters()[0])
        .build();
  }

  @Test void findOneDirectSucceeds() {
    Unit unit =
      path.find(Unit.class, DIRECT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(2, unit.value());
  }

  @Test void findOneDirectFails() {
    path.find(Iota.class, DIRECT)
      .ifPresent(i -> fail("Iota should not be directly present here"));
  }

  @Test void findAllDirectOrIndirectSucceeds() {
    List<Unit> units = path.findAll(Unit.class, DIRECT_OR_INDIRECT);

    assertThat(units, containsInAnyOrder(unitOfValue(2)));
  }

  @Test void findAllDirectOrIndirectFails() {
    assertEquals(emptyList(), path.findAll(Atom.class, DIRECT_OR_INDIRECT));
  }

  @Test void findOnePresentSucceeds() {
    Unit unit =
      path.find(Unit.class, PRESENT)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(2, unit.value());
  }

  @Test void findOnePresentFails() {
    path.find(Iota.class, PRESENT)
      .ifPresent(i -> fail("Iota should not be directly present here"));
  }

  @Test void findAllAssociatedSucceeds() {
    List<Unit> units = path.findAll(Unit.class, ASSOCIATED);

    assertThat(units, containsInAnyOrder(unitOfValue(2)));
  }

  @Test void findAllAssociatedFails() {
    assertEquals(emptyList(), path.findAll(Atom.class, ASSOCIATED));
  }
}
