package com.pholser.annogami;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
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

  @Retention(RUNTIME)
  @interface Base {
    String value() default "";
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

  @Test
  void allWithAliasingIncludesSynthesizedMetaAnnotationsFromAllPathElements() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(AlphaComposed.class, BetaComposed.class));

    List<Annotation> all = path.all(META_DIRECT, Aliasing.spring());

    List<String> baseValues = all.stream()
      .filter(a -> a.annotationType() == Base.class)
      .map(a -> ((Base) a).value())
      .toList();

    assertThat(baseValues).containsExactly("alpha", "beta");
  }
}
