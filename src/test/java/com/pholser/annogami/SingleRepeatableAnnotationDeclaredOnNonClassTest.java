package com.pholser.annogami;

import com.pholser.annogami.annotated.X;
import com.pholser.annogami.annotations.Particle;
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

class SingleRepeatableAnnotationDeclaredOnNonClassTest {
  private AnnotatedElement target;

  @BeforeEach void setUp() throws Exception {
    target = X.class.getDeclaredField("s");
  }

  @Test void findDirect() {
    Particle p =
      DIRECT.find(Particle.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(4, p.value());
  }

  @Test void allDirect() {
    assertThat(DIRECT.all(target))
      .containsExactlyInAnyOrder(annoValue(Particle.class, 4));
  }

  @Test void findAllDirectOrIndirect() {
    assertThat(DIRECT_OR_INDIRECT.find(Particle.class, target))
      .containsExactlyInAnyOrder(annoValue(Particle.class, 4));
  }

  @Test void findPresent() {
    Particle p =
      PRESENT.find(Particle.class, target)
        .orElseGet(Assertions::fail);

    assertEquals(4, p.value());
  }

  @Test void allPresent() {
    assertThat(PRESENT.all(target))
      .containsExactlyInAnyOrder(annoValue(Particle.class, 4));
  }

  @Test void findAllAssociated() {
    assertThat(ASSOCIATED.find(Particle.class, target))
      .containsExactlyInAnyOrder(annoValue(Particle.class, 4));
  }
}
