package com.pholser.annogami.indirect;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectOrIndirectPresenceOnRecordComponentTest {
  @Retention(RUNTIME)
  @Target(RECORD_COMPONENT)
  @interface A {
    int value();
  }

  @Retention(RUNTIME)
  @Target(RECORD_COMPONENT)
  @interface Bs {
    B[] value();
  }

  @Retention(RUNTIME)
  @Target(RECORD_COMPONENT)
  @Repeatable(Bs.class)
  @interface B {
    int value();
  }

  record R(@A(1) int a, @B(2) @B(3) int b, int c) {
  }

  @Test
  void findsSingleNonRepeatable() {
    assertThat(
      DIRECT_OR_INDIRECT.find(A.class, R.class.getRecordComponents()[0]))
      .singleElement()
      .extracting(A::value)
      .isEqualTo(1);
  }

  @Test
  void findsRepeatable() {
    List<B> bs =
      DIRECT_OR_INDIRECT.find(B.class, R.class.getRecordComponents()[1]);

    assertThat(bs)
      .extracting(B::value)
      .containsExactlyInAnyOrder(2, 3);
  }

  @Test
  void findsContainerAnnotationOfRepeatable() {
    assertThat(
      DIRECT_OR_INDIRECT.find(Bs.class, R.class.getRecordComponents()[1]))
      .singleElement()
      .satisfies(bs ->
        assertThat(bs.value())
          .extracting(B::value)
          .containsExactlyInAnyOrder(2, 3));
  }

  @Test
  void missesNonDeclared() {
    List<A> as =
      DIRECT_OR_INDIRECT.find(A.class, R.class.getRecordComponents()[2]);

    assertThat(as).isEmpty();
  }
}
