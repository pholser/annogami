package com.pholser.annogami.programmatic;

import com.pholser.annogami.Aliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;
import java.util.Optional;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProgrammaticAliasingTest {
  @Retention(RUNTIME)
  @interface Route {
    String path() default "";
  }

  @Retention(RUNTIME)
  @interface GetMapping {
    String value() default "";

    String path() default "";
  }

  @Test
  void nonDefaultValuePropagatesFromSourceToTarget() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .build();

    Optional<Route> result = aliasing.synthesize(
      Route.class,
      List.of(fakeGetMapping("/users", "")));

    Route r = result.orElseGet(Assertions::fail);
    assertThat(r.path()).isEqualTo("/users");
  }

  @Test
  void defaultValueIsNotPropagated() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .build();

    Optional<Route> result = aliasing.synthesize(
      Route.class,
      List.of(fakeGetMapping("", "")));

    assertThat(result).isEmpty();
  }

  @Test
  void returnsEmptyWhenSourceAnnotationAbsentFromContext() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .build();

    Optional<Route> result = aliasing.synthesize(
      Route.class,
      List.of());

    assertThat(result).isEmpty();
  }

  @Retention(RUNTIME)
  @interface Other {
    String x() default "";
  }

  @Test
  void returnsEmptyWhenNoEdgesTargetTheRequestedType() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .build();

    Optional<Other> result = aliasing.synthesize(
      Other.class,
      List.of(fakeGetMapping("/users", "")));

    assertThat(result).isEmpty();
  }

  // --- Multiple sources for the same target attribute: first non-default wins ---

  @Retention(RUNTIME)
  @interface PostMapping {
    String value() default "";
  }

  @Test
  void firstNonDefaultSourceValueWins() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .alias(PostMapping.class, "value", Route.class, "path")
      .build();

    // GetMapping has a non-default value; PostMapping also does.
    // GetMapping appears first in the context list.
    Optional<Route> result = aliasing.synthesize(
      Route.class,
      List.of(fakeGetMapping("/get", ""), fakePostMapping("/post")));

    Route r = result.orElseGet(Assertions::fail);
    assertThat(r.path()).isEqualTo("/get");
  }

  @Test
  void secondSourceUsedWhenFirstSourceValueIsDefault() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .alias(PostMapping.class, "value", Route.class, "path")
      .build();

    Optional<Route> result = aliasing.synthesize(
      Route.class,
      List.of(fakeGetMapping("", ""), fakePostMapping("/post")));

    Route r = result.orElseGet(Assertions::fail);
    assertThat(r.path()).isEqualTo("/post");
  }

  @Retention(RUNTIME)
  @interface Http {
    String verb() default "";

    String url() default "";
  }

  @Retention(RUNTIME)
  @interface Endpoint {
    String method() default "";

    String path() default "";
  }

  @Retention(RUNTIME)
  @interface Typed {
    int count() default 0;
  }

  @Retention(RUNTIME)
  @interface RequiredSource {
    String value(); // no default
  }

  @Retention(RUNTIME)
  @interface RequiredTarget {
    String path(); // no default
  }

  @Test
  void multipleTargetAttributesEachPropagatedIndependently() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(Http.class, "verb", Endpoint.class, "method")
      .alias(Http.class, "url", Endpoint.class, "path")
      .build();

    Http http = new Http() {
      public String verb() { return "GET"; }
      public String url() { return "/items"; }
      public Class<? extends Annotation> annotationType() { return Http.class; }
    };

    Optional<Endpoint> result = aliasing.synthesize(Endpoint.class, List.of(http));

    assertThat(result)
      .isPresent()
      .hasValueSatisfying(e -> {
        assertThat(e.method()).isEqualTo("GET");
        assertThat(e.path()).isEqualTo("/items");
      });
  }

  @Test
  void throwsWhenSourceAttributeDoesNotExist() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "nonexistent", Route.class, "path")
        .build())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("nonexistent");
  }

  @Test
  void throwsWhenTargetAttributeDoesNotExist() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", Route.class, "nonexistent")
        .build())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("nonexistent");
  }

  @Test
  void throwsWhenSourceAttributeHasNoDefault() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(RequiredSource.class, "value", Route.class, "path")
        .build())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("default");
  }

  @Test
  void throwsWhenTargetAttributeHasNoDefault() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", RequiredTarget.class, "path")
        .build())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("default");
  }

  @Test
  void throwsWhenSourceAndTargetAttributeTypesAreIncompatible() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", Typed.class, "count")
        .build())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("incompatible");
  }

  @Test
  void registeringSameEdgeTwiceIsIdempotent() {
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .alias(GetMapping.class, "value", Route.class, "path")
      .build();

    Optional<Route> result = aliasing.synthesize(
      Route.class,
      List.of(fakeGetMapping("/users", "")));

    Route r = result.orElseGet(Assertions::fail);
    assertThat(r.path()).isEqualTo("/users");
  }

  @Test
  void twoSourceAttributesOnSameTypeCanFeedSameTargetAttribute() {
    // GetMapping.value and GetMapping.path both alias to Route.path;
    // value is default, path is non-default — path should win
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .alias(GetMapping.class, "path",  Route.class, "path")
      .build();

    Optional<Route> result = aliasing.synthesize(
      Route.class,
      List.of(fakeGetMapping("", "/items")));

    Route r = result.orElseGet(Assertions::fail);
    assertThat(r.path()).isEqualTo("/items");
  }

  @Test
  void oneSourceAttributeCanFeedTwoTargetAttributes() {
    // GetMapping.value feeds both Endpoint.method and Endpoint.path
    Aliasing aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Endpoint.class, "method")
      .alias(GetMapping.class, "value", Endpoint.class, "path")
      .build();

    Http http = new Http() {
      public String verb() { return ""; }
      public String url()  { return ""; }
      public Class<? extends Annotation> annotationType() { return Http.class; }
    };

    Endpoint result =
      aliasing.synthesize(
        Endpoint.class,
        List.of(fakeGetMapping("/orders", ""), http)
      ).orElseGet(Assertions::fail);

      assertThat(result.method()).isEqualTo("/orders");
      assertThat(result.path()).isEqualTo("/orders");
  }

  @Test
  void throwsOnNullSourceTypeInAlias() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(null, "value", Route.class, "path")
        .build())
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void throwsOnNullSourceAttrInAlias() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, null, Route.class, "path")
        .build())
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void throwsOnNullTargetTypeInAlias() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", null, "path")
        .build())
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void throwsOnNullTargetAttrInAlias() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", Route.class, null)
        .build())
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void throwsOnNullAnnoTypeInSynthesize() {
    Aliasing aliasing =
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", Route.class, "path")
        .build();

    assertThatThrownBy(() -> aliasing.synthesize(null, List.of()))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void throwsOnNullMetaContextInSynthesize() {
    Aliasing aliasing =
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", Route.class, "path")
        .build();

    assertThatThrownBy(() -> aliasing.synthesize(Route.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void throwsOnSelfAlias() {
    assertThatThrownBy(() ->
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", GetMapping.class, "value")
        .build())
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void toStringFollowsAnnotationToStringConvention() {
    Aliasing aliasing =
      ProgrammaticAliasing.builder()
        .alias(GetMapping.class, "value", Route.class, "path")
        .build();

    Route synthesized =
      aliasing.synthesize(
        Route.class,
        List.of(fakeGetMapping("/users", "")))
      .orElseGet(Assertions::fail);

    String s = synthesized.toString();

    assertThat(s)
      .startsWith("@" + Route.class.getName() + "(")
      .endsWith(")")
      .contains("path=\"/users\"");
  }

  // --- Helpers ---

  private static GetMapping fakeGetMapping(String value, String path) {
    return new GetMapping() {
      public String value() { return value; }
      public String path() { return path; }
      public Class<? extends Annotation> annotationType() { return GetMapping.class; }
    };
  }

  private static PostMapping fakePostMapping(String value) {
    return new PostMapping() {
      public String value() { return value; }
      public Class<? extends Annotation> annotationType() { return PostMapping.class; }
    };
  }
}
