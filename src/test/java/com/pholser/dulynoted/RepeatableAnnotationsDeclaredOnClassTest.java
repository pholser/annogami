package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static com.pholser.dulynoted.AssertionHelp.failure;
import static com.pholser.dulynoted.annotations.AnnotationMatching.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RepeatableAnnotationsDeclaredOnClassTest {
  private AnnotatedElement target;

  @BeforeEach void setUp() {
    target = Y.class;
  }

  @Test void findOneKindDirect() {
    new DirectPresence().find(Particle.class, target)
      .ifPresent(p -> fail("Single Particle should not be found"));
  }

  @Test void findOneContainerKindDirect() {
    Compound c =
      new DirectPresence().find(Compound.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        particleOfValue(-1),
        particleOfValue(-2)));
  }

  @Test void findAnotherKindDirect() {
    Unit u =
      new DirectPresence().find(Unit.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertEquals(-3, u.value());
  }

  @Test void findAnotherContainerKindDirect() {
    new DirectPresence().find(Aggregate.class, target)
      .ifPresent(p ->
        fail("Aggregate annotation should not be found"));
  }

  @Test void allDirect() {
    List<Annotation> all = new DirectPresence().all(target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundAnnotationWith(
          particleOfValue(-1),
          particleOfValue(-2)),
        unitAnnotationOfValue(-3)));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    List<Particle> all =
      new DirectOrIndirectPresence().findAll(Particle.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        particleOfValue(-1),
        particleOfValue(-2)));
  }

  @Test void findAllOneContainerKindDirectOrIndirect() {
    List<Compound> all =
      new DirectOrIndirectPresence().findAll(Compound.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundWith(
          particleOfValue(-1),
          particleOfValue(-2))));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    List<Unit> all =
      new DirectOrIndirectPresence().findAll(Unit.class, target);

    assertThat(
      all,
      containsInAnyOrder(unitOfValue(-3)));
  }

  @Test void findAllAnotherContainerKindDirectOrIndirect() {
    List<Aggregate> all =
      new DirectOrIndirectPresence().findAll(Aggregate.class, target);

    assertEquals(emptyList(), all);
  }

  @Test void findOneKindPresent() {
    Particle p =
      new Presence().find(Particle.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertEquals(-6, p.value());
  }

  @Test void findOneContainerKindPresent() {
    Compound c =
      new Presence().find(Compound.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        particleOfValue(-1),
        particleOfValue(-2)));
  }

  @Test void findAnotherKindPresent() {
    Unit u =
      new Presence().find(Unit.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertEquals(-3, u.value());
  }

  @Test void findAnotherContainerKindPresent() {
    Aggregate a =
      new Presence().find(Aggregate.class, target)
        .orElseThrow(failure("Missing annotation"));

    assertThat(
      asList(a.value()),
      containsInAnyOrder(
        unitOfValue(-4),
        unitOfValue(-5)));
  }

  @Test void allPresent() {
    List<Annotation> all = new Presence().all(target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundAnnotationWith(
          particleOfValue(-1),
          particleOfValue(-2)),
        unitAnnotationOfValue(-3),
        aggregateAnnotationWith(
          unitOfValue(-4),
          unitOfValue(-5)),
        particleAnnotationOfValue(-6),
        manyAnnotationWith(
          singleOfValue(-7),
          singleOfValue(-8)
        )));
  }

  @Test void findAllOneKindAssociated() {
    List<Particle> all =
      new AssociatedPresence().findAll(Particle.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        particleOfValue(-1),
        particleOfValue(-2)));
  }

  @Test void findAllOneContainerKindAssociated() {
    List<Compound> all =
      new AssociatedPresence().findAll(Compound.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        compoundWith(
          particleOfValue(-1),
          particleOfValue(-2))));
  }

  @Test void findAllAnotherKindAssociated() {
    List<Single> all =
      new AssociatedPresence().findAll(Single.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        singleOfValue(-7),
        singleOfValue(-8)));
  }

  @Test void findAllAnotherContainerKindAssociated() {
    List<Many> all =
      new AssociatedPresence().findAll(Many.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        manyWith(
          singleOfValue(-7),
          singleOfValue(-8))));
  }
}
