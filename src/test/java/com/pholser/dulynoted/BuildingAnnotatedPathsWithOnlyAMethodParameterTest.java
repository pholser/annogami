package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.AnnotatedParameterHaver;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import com.pholser.dulynoted.annotations.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pholser.dulynoted.AssertionHelp.*;
import static com.pholser.dulynoted.Presences.*;
import static com.pholser.dulynoted.annotations.AnnotationMatching.*;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class BuildingAnnotatedPathsWithOnlyAMethodParameterTest {
  private AnnotatedPath path;

  @BeforeEach
  void setUp() throws Exception {
    path =
      AnnotatedPath.fromParameter(
        AnnotatedParameterHaver.class
          .getMethod("x", int.class)
          .getParameters()[0])
        .build();
  }

  @Test
  void findOneDirectSucceeds() {
    Atom atom =
      path.find(Atom.class, DIRECT)
        .orElseThrow(failure("Missing annotation"));

    assertEquals(1, atom.value());
  }

  @Test
  void findOneDirectFails() {
    path.find(Iota.class, DIRECT)
      .ifPresent(i -> fail("Iota should not be directly present here"));
  }

  @Test
  void findAllDirectOrIndirectSucceeds() {
    List<Atom> atoms = path.findAll(Atom.class, DIRECT_OR_INDIRECT);

    assertThat(atoms, containsInAnyOrder(atomOfValue(1)));
  }

  @Test
  void findAllDirectOrIndirectFails() {
    assertEquals(emptyList(), path.findAll(Unit.class, DIRECT_OR_INDIRECT));
  }
}
