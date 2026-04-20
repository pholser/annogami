package com.pholser.annogami;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathAliasingTest {
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
  static class Alpha {
  }

  @Composed(name = "beta")
  static class Beta {
  }

  @Test
  void allWithAliasingAppliesAliasesToEachElement() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    List<Annotation> all =
      path.all(DIRECT, SpringAliasing.aliasing());

    List<Composed> composed =
      all.stream()
        .filter(a -> a.annotationType() == Composed.class)
        .map(Composed.class::cast)
        .toList();

    assertThat(composed).hasSize(2);
    assertThat(composed.get(0).name()).isEqualTo("alpha");
    assertThat(composed.get(1).name()).isEqualTo("beta");
  }

  @Test
  void findWithAliasingAppliesAliasesToEachElement() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    List<Base> found =
      path.find(Base.class, META_DIRECT_OR_INDIRECT, SpringAliasing.aliasing());

    assertThat(found).hasSize(2);
    assertThat(found.get(0).value()).isEqualTo("alpha");
    assertThat(found.get(1).value()).isEqualTo("beta");
  }

  @Test
  void findFirstWithAliasingReturnsFirstSynthesizedMatch() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    assertThat(
      path.findFirst(Base.class, META_DIRECT, SpringAliasing.aliasing()))
      .isPresent()
      .hasValueSatisfying(b -> assertThat(b.value()).isEqualTo("alpha"));
  }
}
