package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAliasForTransitiveMetaOverrideCycleTest {
  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Base
  @interface Level1 {
    @AliasFor(annotation = Base.class, attribute = "value")
    String x() default "";

    // Cross-annotation cycle: Level1.xAlias <-> Composed.z
    @AliasFor(annotation = Composed.class, attribute = "z")
    String xAlias() default "";
  }

  @Retention(RUNTIME)
  @Level1
  @interface Composed {
    @AliasFor(annotation = Level1.class, attribute = "xAlias")
    String z() default "";
  }

  @Composed(z = "boom")
  static class Target {
  }

  @Test
  void cycleInAliasGraphIsDetectedAndFailsFast() {
    assertThatThrownBy(() ->
      META_DIRECT.find(Base.class, Target.class, spring())
        .orElseThrow()
        .value() // force proxy creation / member access
    ).isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("cycle");
  }
}
