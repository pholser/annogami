package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Three composed attributes all aliasing the same meta-annotation attribute
 * form a 3-element implicit intra-alias group. Setting any one of the three
 * must propagate to all three (and to the meta attribute).
 */
class SpringAliasForThreeWayImplicitIntraAliasTest {
  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Base
  @interface Route {
    @AliasFor(annotation = Base.class, attribute = "value")
    String url() default "";

    @AliasFor(annotation = Base.class, attribute = "value")
    String path() default "";

    @AliasFor(annotation = Base.class, attribute = "value")
    String value() default "";
  }

  @Route(url = "/api")
  static class SetViaUrl {
  }

  @Route(path = "/api")
  static class SetViaPath {
  }

  @Route(value = "/api")
  static class SetViaValue {
  }

  @Route(url = "/a", path = "/b")
  static class ConflictUrlAndPath {
  }

  @Route(url = "/a", value = "/b")
  static class ConflictUrlAndValue {
  }

  @Test
  void settingUrlPropagatesAllThreeMembers() {
    Route r = DIRECT.find(Route.class, SetViaUrl.class, SpringAliasing.aliasing())
      .orElseGet(Assertions::fail);

    assertThat(r.url()).isEqualTo("/api");
    assertThat(r.path()).isEqualTo("/api");
    assertThat(r.value()).isEqualTo("/api");
  }

  @Test
  void settingPathPropagatesAllThreeMembers() {
    Route r = DIRECT.find(Route.class, SetViaPath.class, SpringAliasing.aliasing())
      .orElseGet(Assertions::fail);

    assertThat(r.url()).isEqualTo("/api");
    assertThat(r.path()).isEqualTo("/api");
    assertThat(r.value()).isEqualTo("/api");
  }

  @Test
  void settingValuePropagatesAllThreeMembers() {
    Route r = DIRECT.find(Route.class, SetViaValue.class, SpringAliasing.aliasing())
      .orElseGet(Assertions::fail);

    assertThat(r.url()).isEqualTo("/api");
    assertThat(r.path()).isEqualTo("/api");
    assertThat(r.value()).isEqualTo("/api");
  }

  @Test
  void settingUrlAlsoPropagatesMetaAttribute() {
    Base b = META_DIRECT.find(Base.class, SetViaUrl.class, SpringAliasing.aliasing())
      .orElseGet(Assertions::fail);

    assertThat(b.value()).isEqualTo("/api");
  }

  @Test
  void conflictingExplicitValuesOnUrlAndPathFailFast() {
    assertThatThrownBy(() ->
      DIRECT.find(Route.class, ConflictUrlAndPath.class, SpringAliasing.aliasing())
        .orElseGet(Assertions::fail)
        .url()
    ).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void conflictingExplicitValuesOnUrlAndValueFailFast() {
    assertThatThrownBy(() ->
      DIRECT.find(Route.class, ConflictUrlAndValue.class, SpringAliasing.aliasing())
        .orElseGet(Assertions::fail)
        .value()
    ).isInstanceOf(IllegalStateException.class);
  }
}
