package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllByTypeTransitiveMetaOverrideTest {
  @Retention(RUNTIME)
  @Target(TYPE)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Base
  @interface Level1 {
    @AliasFor(annotation = Base.class, attribute = "value")
    String x() default "";
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Level1
  @interface Composed {
    @AliasFor(annotation = Level1.class, attribute = "x")
    String y() default "";
  }

  @Composed(y = "hello")
  static class Subject {
  }

  @Test
  void transitiveAliasOverridesBaseThroughIntermediateMetaAnnotation() {
    assertThat(
      META_DIRECT_OR_INDIRECT.find(Level1.class, Subject.class, spring()))
      .singleElement()
      .extracting(Level1::x)
      .isEqualTo("hello");

    assertThat(
      META_DIRECT_OR_INDIRECT.find(Base.class, Subject.class, spring()))
      .singleElement()
      .extracting(Base::value)
      .isEqualTo("hello");
  }
}
