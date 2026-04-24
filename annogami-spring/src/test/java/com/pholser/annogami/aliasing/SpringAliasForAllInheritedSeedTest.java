package com.pholser.annogami.aliasing;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

import static com.pholser.annogami.Presences.META_PRESENT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllInheritedSeedTest {
  @Retention(RUNTIME)
  @Target(TYPE)
  @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Inherited
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "value")
    String name() default "";
  }

  @Composed(name = "hello")
  static class InhBase {
  }

  static class InhDerived extends InhBase {
  }

  @Test
  void allWithAliasingUpgradesMetaAnnotationFromInheritedSeed() {
    List<Annotation> all = META_PRESENT.all(InhDerived.class, spring());

    Optional<Composed> composed =
      all.stream()
        .filter(a -> a.annotationType() == Composed.class)
        .map(Composed.class::cast)
        .findFirst();
    assertThat(composed)
      .hasValueSatisfying(c ->
        assertThat(c.name()).isEqualTo("hello"));

    Optional<Base> base =
      all.stream()
        .filter(a -> a.annotationType() == Base.class)
        .map(Base.class::cast)
        .findFirst();
    assertThat(base)
      .hasValueSatisfying(b ->
        assertThat(b.value()).isEqualTo("hello"));
  }
}
