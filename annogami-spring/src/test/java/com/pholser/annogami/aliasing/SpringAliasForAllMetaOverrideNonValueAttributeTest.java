package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

class SpringAliasForAllMetaOverrideNonValueAttributeTest {
  @Retention(RUNTIME)
  @interface Base {
    String name() default "default-name";

    int count() default 42;
  }

  @Retention(RUNTIME)
  @Base
  @interface Composed {
    @AliasFor(annotation = Base.class, attribute = "name")
    String myName() default "";
  }

  @Composed(myName = "hello")
  static class Target {
  }

  @Test
  void metaAliasForNonValueAttributeIsApplied() {
    var all = META_DIRECT.all(Target.class, SpringAliasing.spring());

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Composed.class)
      .singleElement(type(Composed.class))
      .extracting(Composed::myName)
      .isEqualTo("hello");

    assertThat(all)
      .filteredOn(a -> a.annotationType() == Base.class)
      .singleElement(type(Base.class))
      .satisfies(base -> {
        assertThat(base.name()).isEqualTo("hello");
        assertThat(base.count()).isEqualTo(42);
      });
  }
}
