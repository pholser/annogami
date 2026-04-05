package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllByTypeMetaOverrideDefaultsToValueTest {
  @Retention(RUNTIME) @Target(TYPE) @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME) @Target(TYPE) @Base @interface Composed {
    @AliasFor(annotation = Base.class) String name() default "";
  }

  @Composed(name = "hello") static class Subject {}

  @Test void aliasForAnnotationOnlyDefaultsToTargetValue() {
    List<Base> found =
      META_DIRECT_OR_INDIRECT.find(Base.class, Subject.class, Aliasing.spring());

    Base base = found.stream().findFirst().orElseGet(Assertions::fail);

    assertThat(base.value()).isEqualTo("hello");
  }
}
