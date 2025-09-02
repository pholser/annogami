package com.pholser.annogami;

import com.pholser.annogami.annotated.X;
import com.pholser.annogami.annotations.Aggregate;
import com.pholser.annogami.annotations.Compound;
import com.pholser.annogami.annotations.Particle;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

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
        .orElseGet(Assertions::fail);

    assertThat(c.value())
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, 5),
        annoValue(Particle.class, 6));
  }

  @Test void findAnotherKindDirect() {
    DIRECT.find(Unit.class, target)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void findAnotherContainerKindDirect() {
    Aggregate a =
      DIRECT.find(Aggregate.class, target)
        .orElseGet(Assertions::fail);

    assertThat(a.value())
      .containsExactlyInAnyOrder(
        annoValue(Unit.class, 7),
        annoValue(Unit.class, 8));
  }

  @Test void allDirect() {
    assertThat(DIRECT.all(target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, 5),
          annoValue(Particle.class, 6)),
        containerAnno(
          Aggregate.class,
          Unit.class,
          annoValue(Unit.class, 7),
          annoValue(Unit.class, 8)));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.findAll(Particle.class, target))
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, 5),
        annoValue(Particle.class, 6));
  }

  @Test void findAllOneContainerKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.findAll(Compound.class, target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, 5),
          annoValue(Particle.class, 6)));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.findAll(Unit.class, target))
      .containsExactlyInAnyOrder(
        annoValue(Unit.class, 7),
        annoValue(Unit.class, 8));
  }

  @Test void findAllAnotherContainerKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.findAll(Aggregate.class, target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Aggregate.class,
          Unit.class,
          annoValue(Unit.class, 7),
          annoValue(Unit.class, 8)));
  }

  @Test void findOneKindPresent() {
    PRESENT.find(Particle.class, target)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void findOneContainerKindPresent() {
    Compound c =
      PRESENT.find(Compound.class, target)
        .orElseGet(Assertions::fail);

    assertThat(c.value())
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, 5),
        annoValue(Particle.class, 6));
  }

  @Test void findAnotherKindPresent() {
    PRESENT.find(Unit.class, target)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void findAnotherContainerKindPresent() {
    Aggregate a =
      PRESENT.find(Aggregate.class, target)
        .orElseGet(Assertions::fail);

    assertThat(a.value())
      .containsExactlyInAnyOrder(
        annoValue(Unit.class, 7),
        annoValue(Unit.class, 8));
  }

  @Test void allPresent() {
    assertThat(PRESENT.all(target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, 5),
          annoValue(Particle.class, 6)),
        containerAnno(
          Aggregate.class,
          Unit.class,
          annoValue(Unit.class, 7),
          annoValue(Unit.class, 8)));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(ASSOCIATED.findAll(Particle.class, target))
      .containsExactlyInAnyOrder(
        annoValue(Particle.class, 5),
        annoValue(Particle.class, 6));
  }

  @Test void findAllOneContainerKindAssociated() {
    assertThat(ASSOCIATED.findAll(Compound.class, target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Compound.class,
          Particle.class,
          annoValue(Particle.class, 5),
          annoValue(Particle.class, 6)));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(ASSOCIATED.findAll(Unit.class, target))
      .containsExactlyInAnyOrder(
        annoValue(Unit.class, 7),
        annoValue(Unit.class, 8));
  }

  @Test void findAllAnotherContainerKindAssociated() {
    assertThat(ASSOCIATED.findAll(Aggregate.class, target))
      .containsExactlyInAnyOrder(
        containerAnno(
          Aggregate.class,
          Unit.class,
          annoValue(Unit.class, 7),
          annoValue(Unit.class, 8)));
  }
}
