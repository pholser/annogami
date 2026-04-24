package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

class SpringAliasForAllImplicitIntraAliasesViaSameMetaTargetTest {
  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
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
    assertThat(DIRECT.all(TargetNameOnly.class, spring()))
      .filteredOn(a -> a.annotationType() == Composed.class)
      .singleElement(type(Composed.class))
      .satisfies(c -> {
        assertThat(c.name()).isEqualTo("hello");
        assertThat(c.value()).isEqualTo("hello");
      });
  }

  @Test
  void settingValueAlsoSetsNameBecauseTheyAreImplicitAliases() {
    assertThat(DIRECT.all(TargetValueOnly.class, spring()))
      .filteredOn(a -> a.annotationType() == Composed.class)
      .singleElement(type(Composed.class))
      .satisfies(c -> {
        assertThat(c.value()).isEqualTo("hello");
        assertThat(c.name()).isEqualTo("hello");
      });
  }

  @Test
  void conflictingExplicitValuesOnImplicitAliasesFailFast() {
    assertThatThrownBy(() -> DIRECT.all(TargetConflict.class, spring()))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void metaViewAlsoSeesTheResolvedValue() {
    var all = META_DIRECT.all(TargetNameOnly.class, spring());

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Composed.class)
      .singleElement(type(Composed.class))
      .satisfies(c -> {
        assertThat(c.name()).isEqualTo("hello");
        assertThat(c.value()).isEqualTo("hello");
      });

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Base.class)
      .singleElement(type(Base.class))
      .extracting(Base::value)
      .isEqualTo("hello");
  }
}
