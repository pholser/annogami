package com.pholser.annogami;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathBuilderRecordComponentTest {
  @Retention(RUNTIME)
  @interface Tag {
    String value();
  }

  @Tag("record-level")
  record Point(@Tag("component-level") int x, int y) {
  }

  @Test
  void recordComponentPathIncludesComponentItself() {
    var rc = Point.class.getRecordComponents()[0]; // x

    AnnotatedPath path = AnnotatedPathBuilder.fromRecordComponent(rc).build();

    Tag t = path.findFirst(Tag.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(t.value()).isEqualTo("component-level");
  }

  @Test
  void toDeclaringRecordExtendsPathToTheRecordClass() {
    var rc = Point.class.getRecordComponents()[0]; // x

    List<Tag> tags = AnnotatedPathBuilder.fromRecordComponent(rc)
      .toDeclaringRecord()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("component-level", "record-level");
  }

  @Test
  void unannotatedComponentProducesEmptyFindFirst() {
    var rc = Point.class.getRecordComponents()[1]; // y — no @Tag

    AnnotatedPath path = AnnotatedPathBuilder.fromRecordComponent(rc).build();

    assertThat(path.findFirst(Tag.class, DIRECT)).isEmpty();
  }
}
