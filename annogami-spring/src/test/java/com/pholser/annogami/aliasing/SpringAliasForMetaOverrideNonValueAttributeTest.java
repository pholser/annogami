package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForMetaOverrideNonValueAttributeTest {
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
    assertThat(META_DIRECT.find(Base.class, Target.class, spring()))
      .hasValueSatisfying(b -> {
        assertThat(b.name()).isEqualTo("hello");
        assertThat(b.count()).isEqualTo(42);
      });
  }
}
