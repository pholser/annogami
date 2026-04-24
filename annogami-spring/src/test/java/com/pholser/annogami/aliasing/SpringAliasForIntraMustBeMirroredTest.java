package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAliasForIntraMustBeMirroredTest {
  @Retention(RUNTIME)
  @interface Broken {
    @AliasFor("name") String value() default "";

    String name() default "";
  }

  @Broken(value = "x")
  static class Target {
  }

  @Test
  void intraAliasMustBeReciprocal() {
    assertThatThrownBy(() ->
      DIRECT.find(Broken.class, Target.class, spring())
        .orElseThrow()
        .name() // force resolution
    ).isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("mirror");
  }
}
