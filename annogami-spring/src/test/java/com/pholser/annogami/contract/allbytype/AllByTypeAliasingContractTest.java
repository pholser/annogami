package com.pholser.annogami.contract.allbytype;

import com.pholser.annogami.Aliasing;
import com.pholser.annogami.spring.SpringAliasing;
import com.pholser.annogami.AllByType;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

abstract class AllByTypeAliasingContractTest {
  protected abstract AllByType subject();

  protected abstract boolean honorsInherited();

  @Retention(RUNTIME)
  @Target(TYPE)
  @Inherited
  @interface Intra {
    @AliasFor("name") String value() default "";

    @AliasFor("value") String name() default "";
  }

  @Intra(name = "hello")
  static class HasDirectIntra {
  }

  @Intra(name = "hello")
  static class InhBase {
  }

  static class InhDerived extends InhBase {
  }

  @Test
  final void intraAliasedValuePropagatesOnDirectlyDeclaredAnnotation() {
    List<Intra> found =
      subject().find(Intra.class, HasDirectIntra.class, SpringAliasing.aliasing());

    assertThat(found).hasSize(1);

    assertThat(found.get(0).name()).isEqualTo("hello");
    assertThat(found.get(0).value()).isEqualTo("hello");
  }

  @Test
  final void intraAliasedValuePropagatesOnInheritedAnnotation() {
    List<Intra> found =
      subject().find(Intra.class, InhDerived.class, SpringAliasing.aliasing());

    assertThat(found.isEmpty()).isEqualTo(!honorsInherited());

    if (honorsInherited()) {
      assertThat(found).hasSize(1);
      assertThat(found.get(0).name()).isEqualTo("hello");
      assertThat(found.get(0).value()).isEqualTo("hello");
    }
  }
}
