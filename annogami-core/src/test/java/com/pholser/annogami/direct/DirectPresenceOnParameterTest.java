package com.pholser.annogami.direct;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.reflect.Parameter;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectPresenceOnParameterTest {
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

  static class ParameterHolder {
    void p1(@A(1000) int x) {
    }

    void p2(@B(2000) @B(3000) int x) {
    }

    void p3(int x) {
    }
  }

  @Test
  void findsDirectlyPresent() throws Exception {
    assertThat(DIRECT.find(A.class, parameterOf("p1")))
      .isPresent()
      .hasValueSatisfying(a -> assertThat(a.value()).isEqualTo(1000));
  }

  @Test
  void missesNotDeclared() throws Exception {
    assertThat(DIRECT.find(A.class, parameterOf("p3")))
      .isEmpty();
  }

  @Test
  void missesIndirectlyPresent() throws Exception {
    assertThat(DIRECT.find(B.class, parameterOf("p2")))
      .isEmpty();
  }

  @Test
  void findsContainerAnnotationOfIndirectlyPresent() throws Exception {
    assertThat(DIRECT.find(Bs.class, parameterOf("p2")))
      .isPresent()
      .hasValueSatisfying(bs -> assertThat(bs.value()).hasSize(2));
  }

  private static Parameter parameterOf(String methodName)
    throws NoSuchMethodException {

    return ParameterHolder.class.getDeclaredMethod(methodName, int.class)
      .getParameters()[0];
  }
}
