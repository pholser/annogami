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

class SpringAliasForAllMetaOverrideNonValueAttributeTest {
  @Retention(RUNTIME)
  @interface Base {
    String name() default "default-name";

    int count() default 42;
  }

  @Retention(RUNTIME)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "name")
    String myName() default "";
  }

  @Composed(myName = "hello")
  static class Target {
  }

  @Test
  void metaAliasForNonValueAttributeIsApplied() {
    List<Annotation> all = META_DIRECT.all(Target.class, Aliasing.spring());

    Composed composed =
      all.stream()
        .filter(a -> a.annotationType() == Composed.class)
        .map(Composed.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(composed.myName()).isEqualTo("hello");

    Base base =
      all.stream()
        .filter(a -> a.annotationType() == Base.class)
        .map(Base.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(base.name()).isEqualTo("hello");
    assertThat(base.count()).isEqualTo(42);
  }
}
