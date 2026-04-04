package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static com.pholser.annogami.Presences.PRESENT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllPresentIntraAliasingInheritedTest {
  @Retention(RUNTIME) @Target(TYPE) @Inherited @interface Intra {
    @AliasFor("name") String value() default "";
    @AliasFor("value") String name() default "";
  }

  @Intra(name = "hello") static class InhBase {}

  static class InhDerived extends InhBase {}

  @Test void allWithAliasingPropagatesIntraAliasForInheritedAnnotation() {
    List<Annotation> all = PRESENT.all(InhDerived.class, Aliasing.spring());

    Intra intra =
      all.stream()
        .filter(a -> a.annotationType() == Intra.class)
        .map(Intra.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(intra.name()).isEqualTo("hello");
    assertThat(intra.value()).isEqualTo("hello");
  }
}
