package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathEmptyTest {
  @Retention(RUNTIME)
  @interface Foo {
    String value() default "";
  }

  private final AnnotatedPath empty = new AnnotatedPath(List.of());

  @Test
  void findFirstOnEmptyPath() {
    assertThat(empty.findFirst(Foo.class, DIRECT)).isEmpty();
  }

  @Test
  void mergeOnEmptyPath() {
    assertThat(empty.merge(Foo.class, DIRECT)).isEmpty();
  }

  @Test
  void allOnEmptyPath() {
    assertThat(empty.all(DIRECT)).isEmpty();
  }

  @Test
  void findByTypeOnEmptyPath() {
    assertThat(empty.find(Foo.class, DIRECT_OR_INDIRECT)).isEmpty();
  }
}
