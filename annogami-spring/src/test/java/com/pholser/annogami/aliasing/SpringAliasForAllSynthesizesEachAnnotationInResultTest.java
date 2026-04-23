package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllSynthesizesEachAnnotationInResultTest {
  @Retention(RUNTIME)
  @interface First {
    @AliasFor("alias") String value() default "";

    @AliasFor("value") String alias() default "";
  }

  @Retention(RUNTIME)
  @interface Second {
    @AliasFor("alias") String value() default "";

    @AliasFor("value") String alias() default "";
  }

  @First(value = "foo")
  @Second(value = "bar")
  static class Target {
  }

  @Test
  void eachAnnotationInResultGetsSynthesized() {
    List<Annotation> all = DIRECT.all(Target.class, SpringAliasing.spring());

    First first =
      all.stream()
        .filter(a -> a.annotationType() == First.class)
        .map(First.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(first.value()).isEqualTo("foo");
    assertThat(first.alias()).isEqualTo("foo");

    Second second =
      all.stream()
        .filter(a -> a.annotationType() == Second.class)
        .map(Second.class::cast)
        .findFirst()
        .orElseGet(Assertions::fail);

    assertThat(second.value()).isEqualTo("bar");
    assertThat(second.alias()).isEqualTo("bar");
  }
}
