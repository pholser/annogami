package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForMetaOverrideTest {
  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "value")
    String name() default "";
  }

  @Composed(name = "hello")
  static class Target {
  }

  @Test
  void findsValueFromComposedAttribute() {
    assertThat(META_DIRECT.find(Base.class, Target.class, spring()))
      .hasValueSatisfying(b ->
        assertThat(b.value()).isEqualTo("hello"));
  }
}
