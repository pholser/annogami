package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnnotatedPathBuilderClassTest {
  @Retention(RUNTIME)
  @interface Tag {
    String value();
  }

  @Tag("top")
  interface Top {
  }

  @Tag("left")
  interface Left extends Top {
  }

  @Tag("right")
  interface Right extends Top {
  }

  @Tag("base")
  static class Base implements Left, Right {
  }

  @Tag("sub")
  static class Sub extends Base {
  }

  @Test
  void toBreadthHierarchy() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Sub.class)
        .toBreadthHierarchy()
        .build();

    List<Tag> tags = path.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .contains("base", "top", "left", "right");
  }

  @Test
  void depthAndBreadthHierarchyContainSameTagsForSimpleChain() {
    AnnotatedPath depthPath =
      AnnotatedPathBuilder.fromClass(Sub.class)
        .toDepthHierarchy()
        .build();
    AnnotatedPath breadthPath =
      AnnotatedPathBuilder.fromClass(Sub.class)
        .toBreadthHierarchy()
        .build();

    List<Tag> depthTags = depthPath.find(Tag.class, DIRECT_OR_INDIRECT);
    List<Tag> breadthTags = breadthPath.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(depthTags)
      .extracting(Tag::value)
      .containsExactlyInAnyOrderElementsOf(
        breadthTags.stream()
          .map(Tag::value)
          .toList());
  }

  @Tag("outer")
  static class Outer {
    @Tag("middle")
    static class Middle {
      @Tag("inner")
      static class Inner {
      }
    }
  }

  @Test
  void toClassEnclosure() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Outer.Middle.Inner.class)
        .toClassEnclosure()
        .build();

    List<Tag> tags = path.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("inner", "middle", "outer");
  }

  @Test
  void toClassEnclosureForClassWithNoEnclosingClass() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(String.class)
      .toClassEnclosure()
      .build();

    assertThat(path.find(Tag.class, DIRECT_OR_INDIRECT)).isEmpty();
  }

  @Tag("helper-method")
  static Class<?> createLocalClass() {
    class Local {
    }
    return Local.class;
  }

  @Test
  void toEnclosingMethod() {
    Class<?> localClass = createLocalClass();

    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(localClass)
        .toEnclosingMethod()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("helper-method"));
  }

  @Test
  void toEnclosingMethodForClassWithNoEnclosingMethod() {
    assertThatThrownBy(
      () -> AnnotatedPathBuilder.fromClass(Outer.class).toEnclosingMethod()
    ).isInstanceOf(IllegalStateException.class);
  }

  static class LocalInCtorHolder {
    final Class<?> localClassRef;

    @Tag("ctor-level")
    LocalInCtorHolder() {
      class LocalInCtor {
      }
      localClassRef = LocalInCtor.class;
    }
  }

  @Test
  void toEnclosingConstructor() {
    Class<?> localClass = new LocalInCtorHolder().localClassRef;

    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(localClass)
        .toEnclosingConstructor()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("ctor-level"));
  }

  @Test
  void toEnclosingConstructorForClassWithNoEnclosingConstructor() {
    assertThatThrownBy(
      () -> AnnotatedPathBuilder.fromClass(Outer.class).toEnclosingConstructor()
    ).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void toDeclaringPackage() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Sub.class)
        .toDeclaringPackage()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("sub"));
  }

  @Test
  void toDeclaringModule() {
    // Path: [Sub, unnamed module]. Sub has @Tag("sub").
    // The unnamed module has no annotations; verify Sub's tag is found.
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Sub.class)
        .toDeclaringModule()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("sub"));
  }
}
