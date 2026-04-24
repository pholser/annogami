package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForMetaOverrideDefaultsToValueTest {
  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class) String name() default "";
  }

  @Composed(name = "hello")
  static class Target {
  }

  @Test
  void aliasForAnnotationOnlyDefaultsToTargetValue() {
    assertThat(
      META_DIRECT.find(Base.class, Target.class, SpringAliasing.spring()))
      
      .hasValueSatisfying(
        base -> assertThat(base.value()).isEqualTo("hello"));
  }
}
