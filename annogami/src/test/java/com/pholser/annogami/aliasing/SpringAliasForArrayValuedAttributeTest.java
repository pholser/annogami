package com.pholser.annogami.aliasing;

import com.pholser.annogami.Aliasing;
import org.junit.jupiter.api.Assertions;
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

  @Test
  void arrayValuePropagatesFromValueToBasePackages() {
    ComponentScan cs =
      DIRECT.find(ComponentScan.class, SetViaValue.class, Aliasing.spring())
        .orElseGet(Assertions::fail);

    assertThat(cs.value()).containsExactly("com.example", "com.other");
    assertThat(cs.basePackages()).containsExactly("com.example", "com.other");
  }

  @Test
  void arrayValuePropagatesFromBasePackagesToValue() {
    ComponentScan cs =
      DIRECT.find(ComponentScan.class, SetViaBasePackages.class, Aliasing.spring())
        .orElseGet(Assertions::fail);

    assertThat(cs.basePackages()).containsExactly("com.example", "com.other");
    assertThat(cs.value()).containsExactly("com.example", "com.other");
  }

  @Test
  void arrayValuePropagatesFromComposedToMetaAnnotation() {
    Scan scan =
      META_DIRECT.find(Scan.class, SetViaValue.class, Aliasing.spring())
        .orElseGet(Assertions::fail);

    assertThat(scan.packages()).containsExactly("com.example", "com.other");
  }

  @Test
  void twoSynthesizedAnnotationsWithSameArrayValueAreEqual() {
    ComponentScan cs1 =
      DIRECT.find(ComponentScan.class, SetViaValue.class, Aliasing.spring())
        .orElseGet(Assertions::fail);
    ComponentScan cs2 =
      DIRECT.find(ComponentScan.class, SetViaBasePackages.class, Aliasing.spring())
        .orElseGet(Assertions::fail);

    assertThat(cs1).isEqualTo(cs2);
  }

  @Test
  void twoSynthesizedAnnotationsWithSameArrayValueHaveSameHashCode() {
    ComponentScan cs1 =
      DIRECT.find(ComponentScan.class, SetViaValue.class, Aliasing.spring())
        .orElseGet(Assertions::fail);
    ComponentScan cs2 =
      DIRECT.find(ComponentScan.class, SetViaBasePackages.class, Aliasing.spring())
        .orElseGet(Assertions::fail);

    assertThat(cs1.hashCode()).isEqualTo(cs2.hashCode());
  }
}
