package com.pholser.annogami;

import com.pholser.annogami.annotated.Y;
import com.pholser.annogami.annotations.Aggregate;
import com.pholser.annogami.annotations.Compound;
import com.pholser.annogami.annotations.Many;
import com.pholser.annogami.annotations.Particle;
import com.pholser.annogami.annotations.Single;
import com.pholser.annogami.annotations.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.annogami.Presences.ASSOCIATED;
import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.PRESENT;
import static com.pholser.annogami.annotations.Annotations.annoValue;
import static com.pholser.annogami.annotations.Annotations.containerAnno;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RepeatableAnnotationsDeclaredOnClassTest {
  private AnnotatedElement target;

  @BeforeEach void setUp() {
    target = Y.class;
  }

  @Test void findOneKindDirect() {
    DIRECT.find(Particle.class, target)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void findOneContainerKindDirect() {
    Compound c =
      DIRECT.find(Compound.class, target)
        .orElseGet(Assertions::fail);

    assertThat(c.value())
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2));
  }

  @Test void findAnotherKindDirect() {
    Unit u =
      DIRECT.find(Unit.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(-3, u.value());
  }

  @Test void findAnotherContainerKindDirect() {
    DIRECT.find(Aggregate.class, target)
      .ifPresent(p ->
        fail("Aggregate annotation should not be found"));
  }

  @Test void allDirect() {
    assertThat(DIRECT.all(target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, -1),
          annoValue(Particle.class, -2)),
        annoValue(Unit.class, -3));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.find(Particle.class, target))
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2));
  }

  @Test void findAllOneContainerKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.find(Compound.class, target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, -1),
          annoValue(Particle.class, -2)));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.find(Unit.class, target))
      .containsExactlyInAnyOrder(annoValue(Unit.class, -3));
  }

  @Test void findAllAnotherContainerKindDirectOrIndirect() {
    assertEquals(
      emptyList(),
      DIRECT_OR_INDIRECT.find(Aggregate.class, target));
  }

  @Test void findOneKindPresent() {
    Particle p =
      PRESENT.find(Particle.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(-6, p.value());
  }

  @Test void findOneContainerKindPresent() {
    Compound c =
      PRESENT.find(Compound.class, target)
        .orElseGet(Assertions::fail);

    assertThat(c.value())
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2));
  }

  @Test void findAnotherKindPresent() {
    Unit u =
      PRESENT.find(Unit.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(-3, u.value());
  }

  @Test void findAnotherContainerKindPresent() {
    Aggregate a =
      PRESENT.find(Aggregate.class, target)
        .orElseGet(Assertions::fail);

    assertThat(a.value())
      .containsExactlyInAnyOrder(
        annoValue(Unit.class, -4),
        annoValue(Unit.class, -5));
  }

  @Test void allPresent() {
    assertThat(PRESENT.all(target))
      .containsExactlyInAnyOrder(
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
          annoValue(Single.class, -8)));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(ASSOCIATED.find(Particle.class, target))
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, -1),
        annoValue(Particle.class, -2));
  }

  @Test void findAllOneContainerKindAssociated() {
    assertThat(ASSOCIATED.find(Compound.class, target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, -1),
          annoValue(Particle.class, -2)));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(ASSOCIATED.find(Single.class, target))
      .containsExactlyInAnyOrder(
        annoValue(Single.class, -7),
        annoValue(Single.class, -8));
  }

  @Test void findAllAnotherContainerKindAssociated() {
    assertThat(ASSOCIATED.find(Many.class, target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Many.class,
          Single.class,
          annoValue(Single.class, -7),
          annoValue(Single.class, -8)));
  }
}
