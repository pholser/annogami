package com.pholser.annogami.direct;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectPresenceOnMethodTest {
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

  static class MethodHaver {
    @A(10)
    void m1() {
    }

    @B(20)
    @B(30)
    void m2() {
    }

    void m3() {
    }
  }

  @Test
  void findsDirectlyPresent() throws Exception {
    assertThat(
      DIRECT.find(A.class, MethodHaver.class.getDeclaredMethod("m1")))
      .isPresent()
      .hasValueSatisfying(a -> assertThat(a.value()).isEqualTo(10));
  }

  @Test
  void missesNotDeclared() throws Exception {
    assertThat(DIRECT.find(A.class, MethodHaver.class.getDeclaredMethod("m3")))
      .isEmpty();
  }

  @Test
  void missesIndirectlyPresent() throws Exception {
    assertThat(DIRECT.find(B.class, MethodHaver.class.getDeclaredMethod("m2")))
      .isEmpty();
  }

  @Test
  void findsContainerAnnotationOfIndirectlyPresent() throws Exception {
    assertThat(
      DIRECT.find(Bs.class, MethodHaver.class.getDeclaredMethod("m2")))
      .isPresent()
      .hasValueSatisfying(bs -> assertThat(bs.value()).hasSize(2));
  }
}
