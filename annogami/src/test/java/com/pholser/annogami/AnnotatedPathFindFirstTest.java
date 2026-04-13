package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathFindFirstTest {
  @Retention(RUNTIME)
  @interface Foo {
    String value() default "";
  }

  @Retention(RUNTIME)
  @interface Bar {
  }

  @Foo("alpha")
  static class Alpha {
  }

  @Foo("beta")
  static class Beta {
  }

  @Bar
  static class WithBar {
  }

  @Test
  void findsAnnotationOnFirstMatchingElement() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    assertThat(path.findFirst(Foo.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(f -> assertThat(f.value()).isEqualTo("alpha"));
  }

  @Test
  void skipsElementsWithoutTheAnnotation() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(WithBar.class, Beta.class));

    assertThat(path.findFirst(Foo.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(f -> assertThat(f.value()).isEqualTo("beta"));
  }

  @Test
  void returnsEmptyWhenNoElementHasTheAnnotation() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(WithBar.class));

    assertThat(path.findFirst(Foo.class, DIRECT)).isEmpty();
  }
}
