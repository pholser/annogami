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
  void findFirstOnEmptyPathReturnsEmpty() {
    assertThat(empty.findFirst(Foo.class, DIRECT)).isEmpty();
  }

  @Test
  void mergeOnEmptyPathReturnsEmpty() {
    assertThat(empty.merge(Foo.class, DIRECT)).isEmpty();
  }

  @Test
  void allOnEmptyPathReturnsEmptyList() {
    assertThat(empty.all(DIRECT)).isEmpty();
  }

  @Test
  void findByTypeOnEmptyPathReturnsEmptyList() {
    assertThat(empty.find(Foo.class, DIRECT_OR_INDIRECT)).isEmpty();
  }

}
