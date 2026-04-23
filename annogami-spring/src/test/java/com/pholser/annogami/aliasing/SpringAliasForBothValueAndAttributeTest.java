package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringAliasForBothValueAndAttributeTest {
  @Retention(RUNTIME)
  @interface Route {
    String path() default "";
  }

  // @AliasFor with both value and attribute non-empty — mutually exclusive
  // per Spring's contract. The Java compiler does not prevent this; the
  // runtime check in SpringAliasing.targetAttributeOf() catches it.
  @Retention(RUNTIME)
  @Route
  @interface GetRoute {
    @AliasFor(annotation = Route.class, value = "path", attribute = "path")
    String value() default "";
  }

  @GetRoute("/orders")
  static class OrderHandler {
  }

  @Test
  void throwsWhenAliasForDeclaresBoothValueAndAttribute() {
    assertThatThrownBy(() ->
      META_DIRECT.find(Route.class, OrderHandler.class, SpringAliasing.aliasing()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("both attribute and value");
  }
}
