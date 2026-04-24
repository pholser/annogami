package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAliasForIntraAnnotationTest {
  @Retention(RUNTIME)
  @interface Intra {
    @AliasFor("name") String value() default "";

    @AliasFor("value") String name() default "";
  }

  @Intra(name = "hello")
  static class Target1 {
  }

  @Intra(value = "hello")
  static class Target2 {
  }

  @Intra(name = "one", value = "two")
  static class TargetConflict {
  }

  @Test
  void intraAliasReadsThroughEitherMember() {
    assertThat(
      DIRECT.find(Intra.class, Target1.class, SpringAliasing.spring()))
      
      .hasValueSatisfying(i -> {
        assertThat(i.name()).isEqualTo("hello");
        assertThat(i.value()).isEqualTo("hello");
      });
  }

  @Test
  void intraAliasReadsThroughEitherDirection() {
    assertThat(
      DIRECT.find(Intra.class, Target2.class, SpringAliasing.spring()))
      
      .hasValueSatisfying(i -> {
        assertThat(i.value()).isEqualTo("hello");
        assertThat(i.name()).isEqualTo("hello");
      });
  }

  @Test
  void intraAliasConflictingExplicitValuesFailFast() {
    assertThatThrownBy(() ->
      DIRECT.find(Intra.class, TargetConflict.class, SpringAliasing.spring())
        .orElseThrow()
        .value()
    ).isInstanceOf(IllegalStateException.class);
  }
}
