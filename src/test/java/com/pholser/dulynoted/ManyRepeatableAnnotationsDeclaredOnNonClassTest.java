package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Compound;
import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.Unit;
import com.pholser.dulynoted.annotations.X;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.AssertionHelp.*;
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
    new DirectPresence().find(Particle.class, target)
      .ifPresent(p ->
        fail("Particle should not be directly present here"));
  }

  @Test void findOneContainerKindDirect() {
    Compound c =
      new DirectPresence().find(Compound.class, target)
        .orElseThrow(failure("Missing Compound annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAnotherKindDirect() {
    new DirectPresence().find(Unit.class, target)
      .ifPresent(u -> fail("Unit should not be directly present here"));
  }

  @Test void findAnotherContainerKindDirect() {
    Aggregate a =
      new DirectPresence().find(Aggregate.class, target)
        .orElseThrow(failure("Missing Aggregate annotation"));

    assertThat(
      asList(a.value()),
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void allDirect() {
    List<Annotation> all = new DirectPresence().all(target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundAnnotationWith(
          particleOfValue(5),
          particleOfValue(6)),
        aggregateAnnotationWith(
          unitOfValue(7),
          unitOfValue(8))));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    List<Particle> all =
      new DirectOrIndirectPresence().findAll(Particle.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAllOneContainerKindDirectOrIndirect() {
    List<Compound> all =
      new DirectOrIndirectPresence().findAll(Compound.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundWith(
          particleOfValue(5),
          particleOfValue(6))));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    List<Unit> all =
      new DirectOrIndirectPresence().findAll(Unit.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void findAllAnotherContainerKindDirectOrIndirect() {
    List<Aggregate> all =
      new DirectOrIndirectPresence().findAll(Aggregate.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        aggregateWith(
          unitOfValue(7),
          unitOfValue(8))));
  }

  @Test void findOneKindPresent() {
    new Presence().find(Particle.class, target)
      .ifPresent(p -> fail("Single Particle should not be found"));
  }

  @Test void findOneContainerKindPresent() {
    Compound c =
      new Presence().find(Compound.class, target)
        .orElseThrow(failure("Missing Compound annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAnotherKindPresent() {
    new Presence().find(Unit.class, target)
      .ifPresent(u -> fail("Single Unit should not be found"));
  }

  @Test void findAnotherContainerKindPresent() {
    Aggregate a =
      new Presence().find(Aggregate.class, target)
        .orElseThrow(failure("Missing Aggregate annotation"));

    assertThat(
      asList(a.value()),
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void allPresent() {
    List<Annotation> all = new Presence().all(target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundAnnotationWith(
          particleOfValue(5),
          particleOfValue(6)),
        aggregateAnnotationWith(
          unitOfValue(7),
          unitOfValue(8))));
  }

  @Test void findAllOneKindAssociated() {
    List<Particle> all =
      new AssociatedPresence().findAll(Particle.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        particleOfValue(5),
        particleOfValue(6)));
  }

  @Test void findAllOneContainerKindAssociated() {
    List<Compound> all =
      new AssociatedPresence().findAll(Compound.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundWith(
          particleOfValue(5),
          particleOfValue(6))));
  }

  @Test void findAllAnotherKindAssociated() {
    List<Unit> all =
      new AssociatedPresence().findAll(Unit.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        unitOfValue(7),
        unitOfValue(8)));
  }

  @Test void findAllAnotherContainerKindAssociated() {
    List<Aggregate> all =
      new AssociatedPresence().findAll(Aggregate.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        aggregateWith(
          unitOfValue(7),
          unitOfValue(8))));
  }
}
