package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.pholser.annogami.Presences.META_ASSOCIATED;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasForAllByTypeMetaAssociatedInheritedSeedTest {
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
  void findWithAliasingUpgradesMetaAnnotationFromInheritedSeed() {
    assertThat(
      META_ASSOCIATED.find(Base.class, InhDerived.class, SpringAliasing.spring()))
      .singleElement()
      .extracting(Base::value)
      .isEqualTo("hello");
  }
}
