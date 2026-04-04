package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAliasForAllImplicitIntraAliasesViaSameMetaTargetTest {
  @Retention(RUNTIME) @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME) @Base @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "value")
    String name() default "";

    @AliasFor(annotation = Base.class, attribute = "value")
    String value() default "";
  }

  @Composed(name = "hello") static class TargetNameOnly {}

  @Composed(value = "hello") static class TargetValueOnly {}

  @Composed(name = "one", value = "two") static class TargetConflict {}

  @Test void settingNameAlsoSetsValueBecauseTheyAreImplicitAliases() {
    List<Annotation> all = DIRECT.all(TargetNameOnly.class, Aliasing.spring());

    Composed c =
      all.stream()
        .filter(a -> a.annotationType() == Composed.class)
        .map(Composed.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(c.name()).isEqualTo("hello");
    assertThat(c.value()).isEqualTo("hello");
  }

  @Test void settingValueAlsoSetsNameBecauseTheyAreImplicitAliases() {
    List<Annotation> all = DIRECT.all(TargetValueOnly.class, Aliasing.spring());

    Composed c =
      all.stream()
        .filter(a -> a.annotationType() == Composed.class)
        .map(Composed.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(c.value()).isEqualTo("hello");
    assertThat(c.name()).isEqualTo("hello");
  }

  @Test void conflictingExplicitValuesOnImplicitAliasesFailFast() {
    assertThatThrownBy(() -> DIRECT.all(TargetConflict.class, Aliasing.spring()))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test void metaViewAlsoSeesTheResolvedValue() {
    List<Annotation> all = META_DIRECT.all(TargetNameOnly.class, Aliasing.spring());

    Base b =
      all.stream()
        .filter(a -> a.annotationType() == Base.class)
        .map(Base.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(b.value()).isEqualTo("hello");
  }
}
