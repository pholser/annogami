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

  // --- toBreadthHierarchy ---

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
  void toBreadthHierarchyIncludesAllSuperclassesAndInterfaces() {
    AnnotatedPath path = AnnotatedPathBuilder.fromClass(Sub.class)
      .toBreadthHierarchy()
      .build();

    List<Tag> tags = path.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags).extracting(Tag::value)
      .contains("base", "top", "left", "right");
  }

  @Test
  void depthAndBreadthHierarchyContainSameTagsForSimpleChain() {
    AnnotatedPath depthPath = AnnotatedPathBuilder.fromClass(Sub.class)
      .toDepthHierarchy()
      .build();
    AnnotatedPath breadthPath = AnnotatedPathBuilder.fromClass(Sub.class)
      .toBreadthHierarchy()
      .build();

    List<Tag> depthTags = depthPath.find(Tag.class, DIRECT_OR_INDIRECT);
    List<Tag> breadthTags = breadthPath.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(depthTags).extracting(Tag::value)
      .containsExactlyInAnyOrderElementsOf(
        breadthTags.stream().map(Tag::value).toList());
  }

  // --- toClassEnclosure ---

  @Tag("outer")
  static class Outer {
    @Tag("middle")
    class Middle {
      @Tag("inner")
      class Inner {
      }
    }
  }

  @Test
  void toClassEnclosureBuildsPathThroughNestingClassesInnermostToOutermost() {
    AnnotatedPath path = AnnotatedPathBuilder
      .fromClass(Outer.Middle.Inner.class)
      .toClassEnclosure()
      .build();

    // Path: [Inner, Middle, Outer] — Inner is in predecessors, Middle and Outer
    // come from the enclosure chain
    List<Tag> tags = path.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("inner", "middle", "outer");
  }

  @Test
  void toClassEnclosureForClassWithNoEnclosingClassProducesEmptyExtension() {
    // String is a genuinely top-level class with no enclosing class
    AnnotatedPath path = AnnotatedPathBuilder
      .fromClass(String.class)
      .toClassEnclosure()
      .build();

    // String has no @Tag, and the enclosure is empty, so nothing found
    assertThat(path.find(Tag.class, DIRECT_OR_INDIRECT)).isEmpty();
  }

  // --- toEnclosingMethod ---

  // A static helper method annotated with @Tag that returns a local class
  // defined inside it. Local classes carry getEnclosingMethod() == this method.
  @Tag("helper-method")
  static Class<?> createLocalClass() {
    class Local {
    }
    return Local.class;
  }

  @Test
  void toEnclosingMethodExtendsPathToTheMethodContainingALocalClass() {
    Class<?> localClass = createLocalClass();

    AnnotatedPath path = AnnotatedPathBuilder.fromClass(localClass)
      .toEnclosingMethod()
      .build();

    // Path: [Local, createLocalClass()]. createLocalClass() has @Tag("helper-method").
    assertThat(path.findFirst(Tag.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(t -> assertThat(t.value()).isEqualTo("helper-method"));
  }

  @Test
  void toEnclosingMethodThrowsForClassWithNoEnclosingMethod() {
    assertThatThrownBy(
      () -> AnnotatedPathBuilder.fromClass(Outer.class).toEnclosingMethod()
    ).isInstanceOf(IllegalStateException.class);
  }

  // --- toEnclosingConstructor ---

  // A helper class whose constructor is annotated with @Tag and captures
  // a local class reference so we can retrieve it in the test.
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
  void toEnclosingConstructorExtendsPathToTheConstructorContainingALocalClass() {
    Class<?> localClass = new LocalInCtorHolder().localClassRef;

    AnnotatedPath path = AnnotatedPathBuilder.fromClass(localClass)
      .toEnclosingConstructor()
      .build();

    // Path: [LocalInCtor, LocalInCtorHolder()]. The constructor has @Tag("ctor-level").
    assertThat(path.findFirst(Tag.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(t -> assertThat(t.value()).isEqualTo("ctor-level"));
  }

  @Test
  void toEnclosingConstructorThrowsForClassWithNoEnclosingConstructor() {
    assertThatThrownBy(
      () -> AnnotatedPathBuilder.fromClass(Outer.class).toEnclosingConstructor()
    ).isInstanceOf(IllegalStateException.class);
  }

  // --- toDeclaringPackage ---

  @Test
  void toDeclaringPackageExtendsPathToTheClassPackage() {
    // Path: [Sub, com.pholser.annogami package]. Sub has @Tag("sub").
    // The package itself may or may not have annotations; verify Sub's tag is found.
    AnnotatedPath path = AnnotatedPathBuilder.fromClass(Sub.class)
      .toDeclaringPackage()
      .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(t -> assertThat(t.value()).isEqualTo("sub"));
  }

  // --- toDeclaringModule ---

  @Test
  void toDeclaringModuleExtendsPathToTheClassModule() {
    // Path: [Sub, unnamed module]. Sub has @Tag("sub").
    // The unnamed module has no annotations; verify Sub's tag is found.
    AnnotatedPath path = AnnotatedPathBuilder.fromClass(Sub.class)
      .toDeclaringModule()
      .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(t -> assertThat(t.value()).isEqualTo("sub"));
  }
}
