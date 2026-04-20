package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathFindTest {
  @Retention(RUNTIME)
  @interface Foo {
    String value() default "";
  }

  @Retention(RUNTIME)
  @interface Bar {
  }

  @Foo("alpha")
  static class Alpha {
  }

  @Foo("beta")
  static class Beta {
  }

  @Bar
  static class WithBar {
  }

  @Test
  void singleElementPathReturnsMatchingAnnotations() {
    AnnotatedPath path = new AnnotatedPath(List.of(Alpha.class));

    List<Foo> found = path.find(Foo.class, DIRECT_OR_INDIRECT);

    assertThat(found).hasSize(1);
    assertThat(found.get(0).value()).isEqualTo("alpha");
  }

  @Test
  void multiElementPathConcatenatesMatchingAnnotationsInOrder() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, Beta.class));

    List<Foo> found = path.find(Foo.class, DIRECT_OR_INDIRECT);

    assertThat(found).hasSize(2);
    assertThat(found.get(0).value()).isEqualTo("alpha");
    assertThat(found.get(1).value()).isEqualTo("beta");
  }

  @Test
  void onlyAnnotationsOfRequestedTypeAreReturned() {
    AnnotatedPath path =
      new AnnotatedPath(List.of(Alpha.class, WithBar.class));

    List<Foo> found = path.find(Foo.class, DIRECT_OR_INDIRECT);

    assertThat(found).hasSize(1);
    assertThat(found.get(0).value()).isEqualTo("alpha");
  }

}
