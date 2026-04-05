package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAliasForAllByTypeTransitiveMetaOverrideCycleTest {
  @Retention(RUNTIME) @Target(TYPE) @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME) @Target(TYPE) @Base @interface Level1 {
    @AliasFor(annotation = Base.class, attribute = "value")
    String x() default "";

    // Cross-annotation cycle: Level1.xAlias <-> Composed.z
    @AliasFor(annotation = Composed.class, attribute = "z")
    String xAlias() default "";
  }

  @Retention(RUNTIME) @Target(TYPE) @Level1 @interface Composed {
    @AliasFor(annotation = Level1.class, attribute = "xAlias")
    String z() default "";
  }

  @Composed(z = "boom") static class Subject {}

  @Test void cycleInAliasGraphIsDetectedAndFailsFast() {
    assertThatThrownBy(
      () -> META_DIRECT_OR_INDIRECT.find(Base.class, Subject.class, Aliasing.spring()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("cycle");
  }
}
