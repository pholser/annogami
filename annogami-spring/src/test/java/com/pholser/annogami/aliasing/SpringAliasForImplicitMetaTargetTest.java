package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForImplicitMetaTargetTest {
  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Base
  @interface Composed {
    @AliasFor("value") String path() default "";
  }

  @Composed(path = "p")
  static class Target {
  }

  @Test
  void aliasWithoutAnnotationDefaultsToMetaAnnotation() {
    assertThat(
      DIRECT.find(Base.class, Target.class, SpringAliasing.spring()))
      .isPresent()
      .hasValueSatisfying(b -> assertThat(b.value()).isEqualTo("p"));
  }
}
