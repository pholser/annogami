package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.reflect.RecordComponent;
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
    RecordComponent rc = Point.class.getRecordComponents()[0];

    AnnotatedPath path = AnnotatedPathBuilder.fromRecordComponent(rc).build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("component-level"));
  }

  @Test
  void toDeclaringRecordExtendsPathToTheRecordClass() {
    RecordComponent rc = Point.class.getRecordComponents()[0];

    List<Tag> tags =
      AnnotatedPathBuilder.fromRecordComponent(rc)
        .toDeclaringRecord()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("component-level", "record-level");
  }

  @Test
  void unannotatedComponentProducesEmptyFindFirst() {
    RecordComponent rc = Point.class.getRecordComponents()[1];

    AnnotatedPath path = AnnotatedPathBuilder.fromRecordComponent(rc).build();

    assertThat(path.findFirst(Tag.class, DIRECT)).isEmpty();
  }
}
