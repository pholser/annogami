package com.pholser.annogami.indirect;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectOrIndirectPresenceOnParameterTest {
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

  static class ParameterHaver {
    void p1(@A(1000) int x) {
    }

    void p2(@B(2000) @B(3000) int x) {
    }

    void p3(int x) {
    }
  }

  @Test
  void findsSingleNonRepeatable() throws Exception {
    assertThat(DIRECT_OR_INDIRECT.find(A.class, parameterOf("p1")))
      .singleElement()
      .extracting(A::value)
      .isEqualTo(1000);
  }

  @Test
  void missesNonDeclared() throws Exception {
    List<A> as = DIRECT_OR_INDIRECT.find(A.class, parameterOf("p3"));

    assertThat(as).isEmpty();
  }

  @Test
  void findsRepeatable() throws Exception {
    List<B> bs = DIRECT_OR_INDIRECT.find(B.class, parameterOf("p2"));

    assertThat(bs)
      .extracting(B::value)
      .containsExactlyInAnyOrder(2000, 3000);
  }

  @Test
  void findsContainerAnnotationOfRepeatable() throws Exception {
    assertThat(DIRECT_OR_INDIRECT.find(Bs.class, parameterOf("p2")))
      .singleElement()
      .satisfies(bs ->
        assertThat(bs.value())
          .extracting(B::value)
          .containsExactlyInAnyOrder(2000, 3000));
  }

  private static Parameter parameterOf(String methodName)
    throws NoSuchMethodException {

    Method m = ParameterHaver.class.getDeclaredMethod(methodName, int.class);
    return m.getParameters()[0];
  }
}
