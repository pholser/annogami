package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

class SpringAliasForAllUpgradesExistingAnnotationsTest {
  @Retention(RUNTIME)
  @Target(TYPE)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Base
  @interface Composed {
    @AliasFor("value") String path() default "";
  }

  @Composed(path = "p")
  static class Subject {
  }

  @Test
  void allWithAliasingUpgradesReturnedAnnotationInstance() {
    List<Annotation> all = META_DIRECT.all(Subject.class, spring());

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Composed.class)
      .singleElement(type(Composed.class))
      .extracting(Composed::path)
      .isEqualTo("p");

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Base.class)
      .singleElement(type(Base.class))
      .extracting(Base::value)
      .isEqualTo("p");
  }
}
