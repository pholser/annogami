package com.pholser.annogami.direct;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectPresenceOnRecordComponentTest {
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
  void findsDirectlyPresent() {
    assertThat(DIRECT.find(A.class, R.class.getRecordComponents()[0]))
      
      .hasValueSatisfying(a -> assertThat(a.value()).isEqualTo(1));
  }

  @Test
  void findsContainerAnnotationOfIndirectlyPresent() {
    assertThat(DIRECT.find(Bs.class, R.class.getRecordComponents()[1]))
      
      .hasValueSatisfying(bs -> assertThat(bs.value()).hasSize(2));
  }

  @Test
  void missesNotDeclared() {
    assertThat(DIRECT.find(A.class, R.class.getRecordComponents()[2]))
      .isEmpty();
  }
}
