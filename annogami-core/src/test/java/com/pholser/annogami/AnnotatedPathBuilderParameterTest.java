package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.reflect.Parameter;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnnotatedPathBuilderParameterTest {
  @Retention(RUNTIME)
  @interface Tag {
    String value();
  }

  @Tag("class-level")
  static class Service {
    @Tag("method-level")
    void perform(@Tag("param-level") String input) {
    }

    @Tag("constructor-level")
    Service(@Tag("constructor-param-level") int id) {
    }
  }

  @Test
  void parameterPathIncludesParameterItself() throws Exception {
    Parameter p =
      Service.class.getDeclaredMethod("perform", String.class)
        .getParameters()[0];

    AnnotatedPath path = AnnotatedPathBuilder.fromParameter(p).build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("param-level"));
  }

  @Test
  void toDeclaringMethodExtendsPathFromParameterToItsMethod() throws Exception {
    Parameter p =
      Service.class.getDeclaredMethod("perform", String.class)
        .getParameters()[0];

    List<Tag> tags =
      AnnotatedPathBuilder.fromParameter(p)
        .toDeclaringMethod()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("param-level", "method-level");
  }

  @Test
  void toDeclaringMethodThenToDeclaringClass() throws Exception {
    Parameter p =
      Service.class.getDeclaredMethod("perform", String.class)
        .getParameters()[0];

    List<Tag> tags =
      AnnotatedPathBuilder.fromParameter(p)
        .toDeclaringMethod()
        .toDeclaringClass()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("param-level", "method-level", "class-level");
  }

  @Test
  void toDeclaringMethodWhenParameterIsOnConstructor() throws Exception {
    Parameter p =
      Service.class.getDeclaredConstructor(int.class)
        .getParameters()[0];

    assertThatThrownBy(
      () -> AnnotatedPathBuilder.fromParameter(p).toDeclaringMethod()
    ).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void toDeclaringConstructorExtendsPathFromParameterToItsConstructor()
    throws Exception {

    Parameter p =
      Service.class.getDeclaredConstructor(int.class)
        .getParameters()[0];

    List<Tag> tags =
      AnnotatedPathBuilder.fromParameter(p)
        .toDeclaringConstructor()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("constructor-param-level", "constructor-level");
  }

  @Test
  void toDeclaringConstructorWhenParameterIsOnMethod() throws Exception {
    Parameter p =
      Service.class.getDeclaredMethod("perform", String.class)
        .getParameters()[0];

    assertThatThrownBy(
      () -> AnnotatedPathBuilder.fromParameter(p).toDeclaringConstructor()
    ).isInstanceOf(IllegalStateException.class);
  }
}
