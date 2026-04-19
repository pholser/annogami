package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathBuilderConstructorAndFieldTest {
  @Retention(RUNTIME)
  @interface Tag {
    String value();
  }

  @Tag("class-level")
  static class Widget {
    @Tag("field-level")
    String name;

    @Tag("constructor-level")
    Widget(String name) {
      this.name = name;
    }
  }

  // --- fromConstructor ---

  @Test
  void constructorPathIncludesConstructorItself() throws Exception {
    var c = Widget.class.getDeclaredConstructor(String.class);

    AnnotatedPath path = AnnotatedPathBuilder.fromConstructor(c).build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(t -> assertThat(t.value()).isEqualTo("constructor-level"));
  }

  @Test
  void toDeclaringClassFromConstructorExtendsPathToItsClass() throws Exception {
    var c = Widget.class.getDeclaredConstructor(String.class);

    List<Tag> tags = AnnotatedPathBuilder.fromConstructor(c)
      .toDeclaringClass()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("constructor-level", "class-level");
  }

  // --- fromField ---

  @Test
  void fieldPathIncludesFieldItself() throws Exception {
    var f = Widget.class.getDeclaredField("name");

    AnnotatedPath path = AnnotatedPathBuilder.fromField(f).build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(t -> assertThat(t.value()).isEqualTo("field-level"));
  }

  @Test
  void toDeclaringClassFromFieldExtendsPathToItsClass() throws Exception {
    var f = Widget.class.getDeclaredField("name");

    List<Tag> tags = AnnotatedPathBuilder.fromField(f)
      .toDeclaringClass()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("field-level", "class-level");
  }
}
