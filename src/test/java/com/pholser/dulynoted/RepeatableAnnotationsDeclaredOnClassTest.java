package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.Y;
import com.pholser.dulynoted.annotations.Aggregate;
import com.pholser.dulynoted.annotations.Compound;
import com.pholser.dulynoted.annotations.Many;
import com.pholser.dulynoted.annotations.Particle;
import com.pholser.dulynoted.annotations.Single;
import com.pholser.dulynoted.annotations.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static com.pholser.dulynoted.Presences.ASSOCIATED;
import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.dulynoted.Presences.PRESENT;
import static com.pholser.dulynoted.annotations.Annotations.annoValue;
import static com.pholser.dulynoted.annotations.Annotations.containerAnno;
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
    DIRECT.find(Particle.class, target)
      .ifPresent(p -> fail("Single Particle should not be found"));
  }

  @Test void findOneContainerKindDirect() {
    Compound c =
      DIRECT.find(Compound.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2)));
  }

  @Test void findAnotherKindDirect() {
    Unit u =
      DIRECT.find(Unit.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(-3, u.value());
  }

  @Test void findAnotherContainerKindDirect() {
    DIRECT.find(Aggregate.class, target)
      .ifPresent(p ->
        fail("Aggregate annotation should not be found"));
  }

  @Test void allDirect() {
    assertThat(
      DIRECT.all(target),
      containsInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, -1),
          annoValue(Particle.class, -2)),
        annoValue(Unit.class, -3)));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Particle.class, target),
      containsInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2)));
  }

  @Test void findAllOneContainerKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Compound.class, target),
      containsInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, -1),
          annoValue(Particle.class, -2))));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertThat(
      DIRECT_OR_INDIRECT.findAll(Unit.class, target),
      containsInAnyOrder(annoValue(Unit.class, -3)));
  }

  @Test void findAllAnotherContainerKindDirectOrIndirect() {
    assertEquals(
      emptyList(),
      DIRECT_OR_INDIRECT.findAll(Aggregate.class, target));
  }

  @Test void findOneKindPresent() {
    Particle p =
      PRESENT.find(Particle.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(-6, p.value());
  }

  @Test void findOneContainerKindPresent() {
    Compound c =
      PRESENT.find(Compound.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertThat(
      asList(c.value()),
      containsInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2)));
  }

  @Test void findAnotherKindPresent() {
    Unit u =
      PRESENT.find(Unit.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(-3, u.value());
  }

  @Test void findAnotherContainerKindPresent() {
    Aggregate a =
      PRESENT.find(Aggregate.class, target)
        .orElseGet(() -> fail("Missing annotation"));

    assertThat(
      asList(a.value()),
      containsInAnyOrder(
        annoValue(Unit.class, -4),
        annoValue(Unit.class, -5)));
  }

  @Test void allPresent() {
    assertThat(
      PRESENT.all(target),
      containsInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, -1),
          annoValue(Particle.class, -2)),
        annoValue(Unit.class, -3),
        containerAnno(
          Aggregate.class,
          Unit.class,
          annoValue(Unit.class, -4),
          annoValue(Unit.class, -5)),
        annoValue(Particle.class, -6),
        containerAnno(
          Many.class,
          Single.class,
          annoValue(Single.class, -7),
          annoValue(Single.class, -8))));
  }

  @Test void findAllOneKindAssociated() {
    List<Particle> all =
      ASSOCIATED.findAll(Particle.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2)));
  }

  @Test void findAllOneContainerKindAssociated() {
    List<Compound> all =
      ASSOCIATED.findAll(Compound.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, -1),
          annoValue(Particle.class, -2))));
  }

  @Test void findAllAnotherKindAssociated() {
    List<Single> all =
      ASSOCIATED.findAll(Single.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        annoValue(Single.class, -7),
        annoValue(Single.class, -8)));
  }

  @Test void findAllAnotherContainerKindAssociated() {
    List<Many> all =
      ASSOCIATED.findAll(Many.class, target);

    assertThat(
      all,
      containsInAnyOrder(
        containerAnno(
          Many.class,
          Single.class,
          annoValue(Single.class, -7),
          annoValue(Single.class, -8))));
  }
}
