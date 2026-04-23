package com.pholser.annogami.contract.all;

import com.pholser.annogami.spring.SpringAliasing;
import com.pholser.annogami.All;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

abstract class AllAliasingContractTest {
  protected abstract All subject();

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
    List<Annotation> all =
      subject().all(HasDirectIntra.class, SpringAliasing.spring());

    Intra intra = all.stream()
      .filter(a -> a.annotationType() == Intra.class)
      .map(Intra.class::cast)
      .findFirst()
      .orElseThrow();

    assertThat(intra.name()).isEqualTo("hello");
    assertThat(intra.value()).isEqualTo("hello");
  }

  @Test
  final void intraAliasedValuePropagatesOnInheritedAnnotation() {
    List<Annotation> all = subject().all(InhDerived.class, SpringAliasing.spring());

    Optional<Intra> maybeIntra = all.stream()
      .filter(a -> a.annotationType() == Intra.class)
      .map(Intra.class::cast)
      .findFirst();

    assertThat(maybeIntra.isPresent()).isEqualTo(honorsInherited());

    maybeIntra.ifPresent(intra -> {
      assertThat(intra.name()).isEqualTo("hello");
      assertThat(intra.value()).isEqualTo("hello");
    });
  }
}
