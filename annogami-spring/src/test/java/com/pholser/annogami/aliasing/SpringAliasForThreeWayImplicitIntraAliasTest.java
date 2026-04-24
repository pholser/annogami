package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
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
    assertThat(
      DIRECT.find(Route.class, SetViaUrl.class, SpringAliasing.spring()))
      
      .hasValueSatisfying(r -> {
        assertThat(r.url()).isEqualTo("/api");
        assertThat(r.path()).isEqualTo("/api");
        assertThat(r.value()).isEqualTo("/api");
      });
  }

  @Test
  void settingPathPropagatesAllThreeMembers() {
    assertThat(
      DIRECT.find(Route.class, SetViaPath.class, SpringAliasing.spring()))
      
      .hasValueSatisfying(r -> {
        assertThat(r.url()).isEqualTo("/api");
        assertThat(r.path()).isEqualTo("/api");
        assertThat(r.value()).isEqualTo("/api");
      });
  }

  @Test
  void settingValuePropagatesAllThreeMembers() {
    assertThat(
      DIRECT.find(Route.class, SetViaValue.class, SpringAliasing.spring()))
      
      .hasValueSatisfying(r -> {
        assertThat(r.url()).isEqualTo("/api");
        assertThat(r.path()).isEqualTo("/api");
        assertThat(r.value()).isEqualTo("/api");
      });
  }

  @Test
  void settingUrlAlsoPropagatesMetaAttribute() {
    assertThat(
      META_DIRECT.find(Base.class, SetViaUrl.class, SpringAliasing.spring()))
      
      .hasValueSatisfying(b -> assertThat(b.value()).isEqualTo("/api"));
  }

  @Test
  void conflictingExplicitValuesOnUrlAndPathFailFast() {
    assertThatThrownBy(() ->
      DIRECT.find(Route.class, ConflictUrlAndPath.class, SpringAliasing.spring())
        .orElseThrow()
        .url()
    ).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void conflictingExplicitValuesOnUrlAndValueFailFast() {
    assertThatThrownBy(() ->
      DIRECT.find(Route.class, ConflictUrlAndValue.class, SpringAliasing.spring())
        .orElseThrow()
        .value()
    ).isInstanceOf(IllegalStateException.class);
  }
}
