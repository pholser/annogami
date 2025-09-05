package com.pholser.annogami;

import com.pholser.annogami.annotated.X;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;

import static com.pholser.annogami.Presences.ASSOCIATED;
import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.PRESENT;
import static com.pholser.annogami.annotations.Annotations.annoValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ManyNonRepeatableAnnotationsDeclaredOnNonClassTest {
  private AnnotatedElement target;

  @BeforeEach void setUp() throws Exception {
    target = X.class.getDeclaredMethod("foo");
  }

  @Test void findOneKindDirect() {
    Atom a =
      DIRECT.find(Atom.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(2, a.value());
  }

  @Test void findAnotherKindDirect() {
    Iota i =
      DIRECT.find(Iota.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(3, i.value());
  }

  @Test void allDirect() {
    assertThat(DIRECT.all(target))
      .containsExactlyInAnyOrder(
        annoValue(Atom.class, 2),
        annoValue(Iota.class, 3));
  }

  @Test void findAllOneKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.find(Atom.class, target))
      .containsExactlyInAnyOrder(annoValue(Atom.class, 2));
  }

  @Test void findAllAnotherKindDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.find(Iota.class, target))
      .containsExactlyInAnyOrder(annoValue(Iota.class, 3));
  }

  @Test void findOneKindPresent() {
    Atom a =
      PRESENT.find(Atom.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(2, a.value());
  }

  @Test void findAnotherKindPresent() {
    Iota i =
      PRESENT.find(Iota.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(3, i.value());
  }

  @Test void allPresent() {
    assertThat(PRESENT.all(target))
      .containsExactlyInAnyOrder(
        annoValue(Atom.class, 2),
        annoValue(Iota.class, 3));
  }

  @Test void findAllOneKindAssociated() {
    assertThat(ASSOCIATED.find(Atom.class, target))
      .containsExactlyInAnyOrder(annoValue(Atom.class, 2));
  }

  @Test void findAllAnotherKindAssociated() {
    assertThat(ASSOCIATED.find(Iota.class, target))
      .containsExactlyInAnyOrder(annoValue(Iota.class, 3));
  }
}
