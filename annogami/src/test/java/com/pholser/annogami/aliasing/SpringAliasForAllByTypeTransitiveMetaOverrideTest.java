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

class SpringAliasForAllByTypeTransitiveMetaOverrideTest {
  @Retention(RUNTIME) @Target(TYPE) @interface Base {
    String value() default "";
  }

  @Retention(RUNTIME) @Target(TYPE) @Base @interface Level1 {
    @AliasFor(annotation = Base.class, attribute = "value")
    String x() default "";
  }

  @Retention(RUNTIME) @Target(TYPE) @Level1 @interface Composed {
    @AliasFor(annotation = Level1.class, attribute = "x")
    String y() default "";
  }

  @Composed(y = "hello") static class Subject {}

  @Test void transitiveAliasOverridesBaseThroughIntermediateMetaAnnotation() {
    List<Level1> level1Found =
      META_DIRECT_OR_INDIRECT.find(Level1.class, Subject.class, Aliasing.spring());

    Level1 level1 = level1Found.stream().findFirst().orElseGet(Assertions::fail);

    assertThat(level1.x()).isEqualTo("hello");

    List<Base> baseFound =
      META_DIRECT_OR_INDIRECT.find(Base.class, Subject.class, Aliasing.spring());

    Base base = baseFound.stream().findFirst().orElseGet(Assertions::fail);

    assertThat(base.value()).isEqualTo("hello");
  }
}
