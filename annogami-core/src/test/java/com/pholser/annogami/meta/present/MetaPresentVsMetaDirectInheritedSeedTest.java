package com.pholser.annogami.meta.present;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.Presences.META_PRESENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class MetaPresentVsMetaDirectInheritedSeedTest {
  @Retention(RUNTIME)
  @interface A {
    int value();
  }

  @A(1)
  @Retention(RUNTIME)
  @Inherited
  @interface HasA {
  }

  @HasA
  static class Base {
  }

  static class Derived extends Base {
  }

  @Test
  void metaPresentSeesInheritedSeedButMetaDirectDoesNot() {
    assertThat(META_DIRECT.find(A.class, Derived.class)).isEmpty();

    assertThat(META_PRESENT.find(A.class, Derived.class))
      .hasValueSatisfying(a ->
        assertThat(a.value()).isEqualTo(1));
  }
}
