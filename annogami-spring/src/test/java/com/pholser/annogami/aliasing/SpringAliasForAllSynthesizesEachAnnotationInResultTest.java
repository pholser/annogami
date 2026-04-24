package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

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
    var all = DIRECT.all(Target.class, SpringAliasing.spring());

    assertThat(all)
      .filteredOn(a -> a.annotationType() == First.class)
      .singleElement(type(First.class))
      .satisfies(first -> {
        assertThat(first.value()).isEqualTo("foo");
        assertThat(first.alias()).isEqualTo("foo");
      });

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Second.class)
      .singleElement(type(Second.class))
      .satisfies(second -> {
        assertThat(second.value()).isEqualTo("bar");
        assertThat(second.alias()).isEqualTo("bar");
      });
  }
}
