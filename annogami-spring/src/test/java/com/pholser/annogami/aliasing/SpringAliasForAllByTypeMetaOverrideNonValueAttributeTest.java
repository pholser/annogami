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

class SpringAliasForAllByTypeMetaOverrideNonValueAttributeTest {
  @Retention(RUNTIME)
  @Target(TYPE)
  @interface Base {
    String name() default "default-name";

    int count() default 42;
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "name")
    String myName() default "";
  }

  @Composed(myName = "hello")
  static class Subject {
  }

  @Test
  void metaAliasForNonValueAttributeIsApplied() {
    assertThat(
      META_DIRECT_OR_INDIRECT.find(
        Base.class, Subject.class, SpringAliasing.spring()))
      .singleElement()
      .satisfies(base -> {
        assertThat(base.name()).isEqualTo("hello");
        assertThat(base.count()).isEqualTo(42);
      });
  }
}
