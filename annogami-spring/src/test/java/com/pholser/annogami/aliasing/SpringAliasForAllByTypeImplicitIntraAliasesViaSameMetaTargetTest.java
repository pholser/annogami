package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAliasForAllByTypeImplicitIntraAliasesViaSameMetaTargetTest {
  @Retention(RUNTIME)
  @Target(TYPE)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "value")
    String name() default "";

    @AliasFor(annotation = Base.class, attribute = "value")
    String value() default "";
  }

  @Composed(name = "hello")
  static class TargetNameOnly {
  }

  @Composed(value = "hello")
  static class TargetValueOnly {
  }

  @Composed(name = "one", value = "two")
  static class TargetConflict {
  }

  @Test
  void settingNameAlsoSetsValueBecauseTheyAreImplicitAliases() {
    assertThat(
      DIRECT_OR_INDIRECT.find(Composed.class, TargetNameOnly.class, spring()))
      .singleElement()
      .satisfies(c -> {
        assertThat(c.name()).isEqualTo("hello");
        assertThat(c.value()).isEqualTo("hello");
      });
  }

  @Test
  void settingValueAlsoSetsNameBecauseTheyAreImplicitAliases() {
    assertThat(
      DIRECT_OR_INDIRECT.find(Composed.class, TargetValueOnly.class, spring()))
      .singleElement()
      .satisfies(c -> {
        assertThat(c.value()).isEqualTo("hello");
        assertThat(c.name()).isEqualTo("hello");
      });
  }

  @Test
  void conflictingExplicitValuesOnImplicitAliases() {
    assertThatThrownBy(
      () -> DIRECT_OR_INDIRECT.find(
        Composed.class, TargetConflict.class, spring()))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void metaViewAlsoSeesTheResolvedValue() {
    assertThat(
      META_DIRECT_OR_INDIRECT.find(Base.class, TargetNameOnly.class, spring()))
      .singleElement()
      .extracting(Base::value)
      .isEqualTo("hello");
  }
}
