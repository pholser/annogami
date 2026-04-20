package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathAllTest {
  @Retention(RUNTIME)
  @interface Foo {
    String value() default "";
  }

  @Foo("alpha")
  static class Alpha {
  }

  @Foo("beta")
  static class Beta {
  }

  @Test
  void singleElementPathReturnsItsAnnotations() {
    AnnotatedPath path = new AnnotatedPath(List.of(Alpha.class));

    List<Annotation> all = path.all(DIRECT);

    assertThat(all).hasSize(1);
    assertThat(((Foo) all.get(0)).value()).isEqualTo("alpha");
  }

  @Test
  void multiElementPathConcatenatesAnnotationsInOrder() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    List<Annotation> all = path.all(DIRECT);

    assertThat(all).hasSize(2);
    assertThat(((Foo) all.get(0)).value()).isEqualTo("alpha");
    assertThat(((Foo) all.get(1)).value()).isEqualTo("beta");
  }

  @Test
  void annotationsOfSameTypeFromBothElementsArePresent() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    List<Annotation> all = path.all(DIRECT);

    assertThat(
      all.stream()
        .filter(a -> a.annotationType() == Foo.class)
        .count())
      .isEqualTo(2);
  }

}
