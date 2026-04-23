package com.pholser.annogami;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
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

    Config c = path.merge(Config.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(c.host()).isEqualTo("alpha.example.com");
    assertThat(c.port()).isEqualTo(8080);
  }

  @Test
  void earlierElementValueWinsForSameAttribute() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Gamma.class));

    Config c = path.merge(Config.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(c.host()).isEqualTo("alpha.example.com");
  }

  @Test
  void laterElementFillsInAttributeLeftAtDefault() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    Config c = path.merge(Config.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(c.host()).isEqualTo("alpha.example.com");
    assertThat(c.port()).isEqualTo(9090);
  }

  @Test
  void returnsEmptyWhenNoElementHasTheAnnotation() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(NoAnnotation.class));

    assertThat(path.merge(Config.class, DIRECT)).isEmpty();
  }

  @Config
  static class AllDefaults {
  }

  @Test
  void mergeWhenAllAttributesAreAtDefaultReturnsAnnotationWithDefaults() {
    AnnotatedPath path = new AnnotatedPath(List.of(AllDefaults.class));

    Config c = path.merge(Config.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(c.host()).isEqualTo("localhost");
    assertThat(c.port()).isEqualTo(8080);
  }

  @Test
  void mergeSkipsUnannotatedElementsAndFallsBackToAnnotatedOnes() {
    AnnotatedPath path = new AnnotatedPath(List.of(NoAnnotation.class, Beta.class));

    Config c = path.merge(Config.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(c.port()).isEqualTo(9090);
  }

}
