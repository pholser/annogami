package com.pholser.annogami.direct;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectPresenceOnFieldTest {
  @Retention(RUNTIME)
  @interface A {
    int value();
  }

  @Retention(RUNTIME)
  @interface Bs {
    B[] value();
  }

  @Retention(RUNTIME)
  @Repeatable(Bs.class)
  @interface B {
    int value();
  }

  static class FieldHaver {
    @A(1)
    int a;
    @B(2)
    @B(3)
    int manyBs;
    int none;
  }

  @Test
  void findsDirectlyPresent() throws Exception {
    assertThat(
      DIRECT.find(
        A.class,
        FieldHaver.class.getDeclaredField("a")))
      .hasValueSatisfying(a ->
        assertThat(a.value()).isEqualTo(1));
  }

  @Test
  void missesNotDeclaredOnField() throws Exception {
    assertThat(
      DIRECT.find(
        A.class,
        FieldHaver.class.getDeclaredField("none")))
      .isEmpty();
  }

  @Test
  void missesIndirectlyPresent() throws Exception {
    assertThat(
      DIRECT.find(
        B.class,
        FieldHaver.class.getDeclaredField("manyBs")))
      .isEmpty();
  }

  @Test
  void findsContainerAnnotationOfIndirectlyPresent() throws Exception {
    assertThat(
      DIRECT.find(
        Bs.class,
        FieldHaver.class.getDeclaredField("manyBs")))
      .hasValueSatisfying(bs ->
        assertThat(bs.value()).hasSize(2));
  }
}
