package com.pholser.annogami;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathMergeTest {
  @Retention(RUNTIME)
  @interface Config {
    String host() default "localhost";

    int port() default 8080;
  }

  @Config(host = "alpha.example.com")
  static class Alpha {
  }

  @Config(port = 9090)
  static class Beta {
  }

  @Config(host = "gamma.example.com", port = 7070)
  static class Gamma {
  }

  static class NoAnnotation {
  }

  @Test
  void mergeOnSingleElementReturnsItsValues() {
    AnnotatedPath path = new AnnotatedPath(List.of(Alpha.class));

    assertThat(path.merge(Config.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(c -> {
        assertThat(c.host()).isEqualTo("alpha.example.com");
        assertThat(c.port()).isEqualTo(8080);
      });
  }

  @Test
  void earlierElementValueWinsForSameAttribute() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Gamma.class));

    assertThat(path.merge(Config.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(c ->
        assertThat(c.host()).isEqualTo("alpha.example.com"));
  }

  @Test
  void laterElementFillsInAttributeLeftAtDefault() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    assertThat(path.merge(Config.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(c -> {
        assertThat(c.host()).isEqualTo("alpha.example.com");
        assertThat(c.port()).isEqualTo(9090);
      });
  }

  @Test
  void returnsEmptyWhenNoElementHasTheAnnotation() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(NoAnnotation.class));

    assertThat(path.merge(Config.class, DIRECT)).isEmpty();
  }

  @Retention(RUNTIME)
  @interface Base {
    String value() default "";

    String extra() default "";
  }

  @Retention(RUNTIME)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "value")
    String name() default "";
  }

  @Composed(name = "alpha")
  static class AlphaComposed {
  }

  @Composed(name = "beta")
  static class BetaComposed {
  }

  @Config
  static class AllDefaults {
  }

  @Test
  void mergeWhenAllAttributesAreAtDefaultReturnsAnnotationWithDefaults() {
    AnnotatedPath path = new AnnotatedPath(List.of(AllDefaults.class));

    assertThat(path.merge(Config.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(c -> {
        assertThat(c.host()).isEqualTo("localhost");
        assertThat(c.port()).isEqualTo(8080);
      });
  }

  @Test
  void mergeSkipsUnannotatedElementsAndFallsBackToAnnotatedOnes() {
    AnnotatedPath path = new AnnotatedPath(List.of(NoAnnotation.class, Beta.class));

    assertThat(path.merge(Config.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(c -> assertThat(c.port()).isEqualTo(9090));
  }

  @Test
  void mergeWithAliasingAppliesAliasesBeforeMerging() {
    AnnotatedPath path = new AnnotatedPath(
      List.of(AlphaComposed.class, BetaComposed.class));

    assertThat(
      path.merge(Base.class, META_DIRECT, Aliasing.spring()))
      .isPresent()
      .hasValueSatisfying(b ->
        assertThat(b.value()).isEqualTo("alpha"));
  }
}
