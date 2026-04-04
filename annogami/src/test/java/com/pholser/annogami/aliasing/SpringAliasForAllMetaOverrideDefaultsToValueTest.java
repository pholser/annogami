package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllMetaOverrideDefaultsToValueTest {
  @Retention(RUNTIME) @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME) @Base @interface Composed {
    @AliasFor(annotation = Base.class) String name() default "";
  }

  @Composed(name = "hello") static class Target {}

  @Test void aliasForAnnotationOnlyDefaultsToTargetValue() {
    List<Annotation> all = META_DIRECT.all(Target.class, Aliasing.spring());

    Composed composed =
      all.stream()
        .filter(a -> a.annotationType() == Composed.class)
        .map(Composed.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(composed.name()).isEqualTo("hello");

    Base base =
      all.stream()
        .filter(a -> a.annotationType() == Base.class)
        .map(Base.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(base.value()).isEqualTo("hello");
  }
}
