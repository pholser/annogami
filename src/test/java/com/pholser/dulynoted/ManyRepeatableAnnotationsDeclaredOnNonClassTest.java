package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Compound;
import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.dulynoted.AssertionHelp.*;
import static com.pholser.dulynoted.Presences.*;
import static com.pholser.dulynoted.annotations.AnnotationMatching.*;
import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ManyRepeatableAnnotationsDeclaredOnNonClassTest {
  private AnnotatedElement target;

  @BeforeEach void setUp() throws Exception {
    target = X.class.getDeclaredMethod("bar");
  }

  @Test void findOneKindDirect() {
    DIRECT.find(Particle.class, target)
      .ifPresent(p ->
        fail("Particle should not be directly present here"));
  }

  @Test void findOneContainerKindDirect() {
    Compound c =
      DIRECT.find(Compound.class, target)
        .orElseThrow(failure("Missing Compound annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAnotherKindDirect() {
    DIRECT.find(Unit.class, target)
      .ifPresent(u -> fail("Unit should not be directly present here"));
  }

  @Test void findAnotherContainerKindDirect() {
    Aggregate a =
      DIRECT.find(Aggregate.class, target)
        .orElseThrow(failure("Missing Aggregate annotation"));

    assertThat(
      asList(a.value()),
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void allDirect() {
    assertThat(
      DIRECT.all(target),
      containsInAnyOrder(
        compoundAnnotationWith(
          particleOfValue(5),
          particleOfValue(6)),
        aggregateAnnotationWith(
          unitOfValue(7),
          unitOfValue(8))));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Particle.class, target),
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAllOneContainerKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Compound.class, target),
      containsInAnyOrder(
        compoundWith(
          particleOfValue(5),
          particleOfValue(6))));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Unit.class, target),
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void findAllAnotherContainerKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Aggregate.class, target),
      containsInAnyOrder(
        aggregateWith(
          unitOfValue(7),
          unitOfValue(8))));
  }

  @Test void findOneKindPresent() {
    PRESENT.find(Particle.class, target)
      .ifPresent(p -> fail("Single Particle should not be found"));
  }

  @Test void findOneContainerKindPresent() {
    Compound c =
      PRESENT.find(Compound.class, target)
        .orElseThrow(failure("Missing Compound annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAnotherKindPresent() {
    PRESENT.find(Unit.class, target)
      .ifPresent(u -> fail("Single Unit should not be found"));
  }

  @Test void findAnotherContainerKindPresent() {
    Aggregate a =
      PRESENT.find(Aggregate.class, target)
        .orElseThrow(failure("Missing Aggregate annotation"));

    assertThat(
      asList(a.value()),
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void allPresent() {
    assertThat(
      PRESENT.all(target),
      containsInAnyOrder(
        compoundAnnotationWith(
          particleOfValue(5),
          particleOfValue(6)),
        aggregateAnnotationWith(
          unitOfValue(7),
          unitOfValue(8))));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Particle.class, target),
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAllOneContainerKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Compound.class, target),
      containsInAnyOrder(
        compoundWith(
          particleOfValue(5),
          particleOfValue(6))));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Unit.class, target),
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void findAllAnotherContainerKindAssociated() {
    assertThat(
      ASSOCIATED.findAll(Aggregate.class, target),
      containsInAnyOrder(
        aggregateWith(
          unitOfValue(7),
          unitOfValue(8))));
  }
}
