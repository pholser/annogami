package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

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
    List<Annotation> all = META_DIRECT.all(Target.class, spring());

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Composed.class)
      .singleElement(type(Composed.class))
      .extracting(Composed::y)
      .isEqualTo("hello");

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Level1.class)
      .singleElement(type(Level1.class))
      .extracting(Level1::x)
      .isEqualTo("hello");

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Base.class)
      .singleElement(type(Base.class))
      .extracting(Base::value)
      .isEqualTo("hello");
  }
}
