package com.pholser.annogami.aliasing;

import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Alias synthesis involving array-typed annotation attributes. Exercises the
 * array-aware equality and hash-code paths in the synthesized proxy.
 */
class SpringAliasForArrayValuedAttributeTest {
  @Retention(RUNTIME)
  @interface Scan {
    String[] packages() default {};
  }

  @Retention(RUNTIME)
  @Scan
  @interface ComponentScan {
    @AliasFor(annotation = Scan.class, attribute = "packages")
    String[] value() default {};

    @AliasFor(annotation = Scan.class, attribute = "packages")
    String[] basePackages() default {};
  }

  @ComponentScan(value = {"com.example", "com.other"})
  static class SetViaValue {
  }

  @ComponentScan(basePackages = {"com.example", "com.other"})
  static class SetViaBasePackages {
  }

  // Both attributes set explicitly — the raw JVM annotation has the same
  // member values as a synthesized ComponentScan where aliasing has propagated
  // the single non-default value to both attributes.
  @ComponentScan(
    value = {"com.example", "com.other"},
    basePackages = {"com.example", "com.other"})
  static class SetViaBoth {
  }

  @Test
  void arrayValuePropagatesFromValueToBasePackages() {
    assertThat(
      DIRECT.find(
        ComponentScan.class, SetViaValue.class, SpringAliasing.spring()))
      .isPresent()
      .hasValueSatisfying(cs -> {
        assertThat(cs.value())
          .containsExactly("com.example", "com.other");
        assertThat(cs.basePackages())
          .containsExactly("com.example", "com.other");
      });
  }

  @Test
  void arrayValuePropagatesFromBasePackagesToValue() {
    assertThat(
      DIRECT.find(
        ComponentScan.class,
        SetViaBasePackages.class,
        SpringAliasing.spring()))
      .isPresent()
      .hasValueSatisfying(cs -> {
        assertThat(cs.basePackages())
          .containsExactly("com.example", "com.other");
        assertThat(cs.value())
          .containsExactly("com.example", "com.other");
      });
  }

  @Test
  void arrayValuePropagatesFromComposedToMetaAnnotation() {
    assertThat(
      META_DIRECT.find(
        Scan.class, SetViaValue.class, SpringAliasing.spring()))
      .isPresent()
      .hasValueSatisfying(scan ->
        assertThat(scan.packages())
          .containsExactly("com.example", "com.other"));
  }

  @Test
  void twoSynthesizedAnnotationsWithSameArrayValueAreEqual() {
    ComponentScan cs1 =
      DIRECT.find(
          ComponentScan.class, SetViaValue.class, SpringAliasing.spring())
        .orElseThrow();
    ComponentScan cs2 =
      DIRECT.find(
          ComponentScan.class,
          SetViaBasePackages.class,
          SpringAliasing.spring())
        .orElseThrow();

    assertThat(cs1).isEqualTo(cs2);
  }

  @Test
  void twoSynthesizedAnnotationsWithSameArrayValueHaveSameHashCode() {
    ComponentScan cs1 =
      DIRECT.find(
          ComponentScan.class, SetViaValue.class, SpringAliasing.spring())
        .orElseThrow();
    ComponentScan cs2 =
      DIRECT.find(
          ComponentScan.class,
          SetViaBasePackages.class,
          SpringAliasing.spring())
        .orElseThrow();

    assertThat(cs1.hashCode()).isEqualTo(cs2.hashCode());
  }

  @Test
  void synthesizedAnnotationEqualsRealAnnotationWithSameValues() {
    ComponentScan real = SetViaBoth.class.getAnnotation(ComponentScan.class);
    assertThat(
      DIRECT.find(
        ComponentScan.class, SetViaValue.class, SpringAliasing.spring()))
      .isPresent()
      .hasValueSatisfying(synthesized ->
        assertThat(synthesized).isEqualTo(real));
  }

  @Test
  void realAnnotationEqualsSynthesizedAnnotationWithSameValues() {
    ComponentScan real = SetViaBoth.class.getAnnotation(ComponentScan.class);
    assertThat(
      DIRECT.find(
        ComponentScan.class, SetViaValue.class, SpringAliasing.spring()))
      .isPresent()
      .hasValueSatisfying(synthesized -> {
        // Symmetry: the JVM's AnnotationInvocationHandler must also
        // consider them equal.
        assertThat(real).isEqualTo(synthesized);
      });
  }

  @Test
  void synthesizedAnnotationAndRealAnnotationWithSameValuesHaveSameHashCode() {
    ComponentScan real = SetViaBoth.class.getAnnotation(ComponentScan.class);
    assertThat(
      DIRECT.find(
        ComponentScan.class, SetViaValue.class, SpringAliasing.spring()))
      .isPresent()
      .hasValueSatisfying(synthesized ->
        assertThat(synthesized.hashCode()).isEqualTo(real.hashCode()));
  }
}
