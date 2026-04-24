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

class SpringAliasForAllByTypeFindUpgradesExistingAnnotationsTest {
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
  void findWithAliasingUpgradesReturnedAnnotationInstance() {
    assertThat(
      META_DIRECT_OR_INDIRECT.find(
        Base.class, Subject.class, SpringAliasing.spring()))
      .singleElement()
      .extracting(Base::value)
      .isEqualTo("p");
  }
}
