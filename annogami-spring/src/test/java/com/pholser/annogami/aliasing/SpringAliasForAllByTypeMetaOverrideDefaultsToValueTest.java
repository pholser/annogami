package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllByTypeMetaOverrideDefaultsToValueTest {
  @Retention(RUNTIME)
  @Target(TYPE)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class) String name() default "";
  }

  @Composed(name = "hello")
  static class Subject {
  }

  @Test
  void aliasForAnnotationOnlyDefaultsToTargetValue() {
    assertThat(
      META_DIRECT_OR_INDIRECT.find(
        Base.class, Subject.class, SpringAliasing.spring()))
      .singleElement()
      .extracting(Base::value)
      .isEqualTo("hello");
  }
}
