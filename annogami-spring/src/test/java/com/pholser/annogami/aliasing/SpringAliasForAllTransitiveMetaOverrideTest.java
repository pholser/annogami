package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllTransitiveMetaOverrideTest {
  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Base
  @interface Level1 {
    @AliasFor(annotation = Base.class, attribute = "value")
    String x() default "";
  }

  @Retention(RUNTIME)
  @Level1
  @interface Composed {
    @AliasFor(annotation = Level1.class, attribute = "x")
    String y() default "";
  }

  @Composed(y = "hello")
  static class Target {
  }

  @Test
  void transitiveAliasOverridesBaseThroughIntermediateMetaAnnotation() {
    List<Annotation> all = META_DIRECT.all(Target.class, SpringAliasing.aliasing());

    Composed composed =
      all.stream()
        .filter(a -> a.annotationType() == Composed.class)
        .map(Composed.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(composed.y()).isEqualTo("hello");

    Level1 level1 =
      all.stream()
        .filter(a -> a.annotationType() == Level1.class)
        .map(Level1.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(level1.x()).isEqualTo("hello");

    Base base =
      all.stream()
        .filter(a -> a.annotationType() == Base.class)
        .map(Base.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(base.value()).isEqualTo("hello");
  }
}
