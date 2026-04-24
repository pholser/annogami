package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathBuilderMethodTest {
  @Retention(RUNTIME)
  @interface Tag {
    String value();
  }

  interface Root {
    @Tag("root")
    void execute();
  }

  static class Middle implements Root {
    @Override
    @Tag("middle")
    public void execute() {
    }
  }

  static class Leaf extends Middle {
    @Override
    public void execute() {
    }
  }

  @Test
  void pathFromMethodAlone() throws Exception {
    Method m = Leaf.class.getDeclaredMethod("execute");

    AnnotatedPath path = AnnotatedPathBuilder.fromMethod(m).build();

    assertThat(path.findFirst(Tag.class, DIRECT)).isEmpty();
  }

  @Test
  void toDepthOverridden() throws Exception {
    Method m = Leaf.class.getDeclaredMethod("execute");

    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(m)
        .toDepthOverridden()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .isPresent()
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("middle"));
  }

  @Test
  void toDepthOverriddenFindsAnnotationFromInterfaceWhenSuperclassHasNone()
    throws Exception {

    // A method with no annotation on the class, but annotated on the interface
    interface Iface {
      @Tag("iface")
      void go();
    }
    class Impl implements Iface {
      @Override
      public void go() {
      }
    }

    Method m = Impl.class.getDeclaredMethod("go");

    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(m)
        .toDepthOverridden()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("iface"));
  }

  @Test
  void toDepthOverriddenFindsNearestOverrideFirst() throws Exception {
    Method m = Leaf.class.getDeclaredMethod("execute");

    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(m)
        .toDepthOverridden()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("middle"));
  }

  @Test
  void toDepthOverriddenCollectsAllTagsAcrossOverrideChain() throws Exception {
    Method m = Leaf.class.getDeclaredMethod("execute");

    List<Tag> tags =
      AnnotatedPathBuilder.fromMethod(m)
        .toDepthOverridden()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("middle", "root");
  }

  @Test
  void staticMethodToDepthOverridden() throws Exception {
    class WithStatic {
      @Tag("static")
      static void staticMethod() {
      }
    }

    Method m = WithStatic.class.getDeclaredMethod("staticMethod");

    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(m)
        .toDepthOverridden()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("static"));

    assertThat(path.find(Tag.class, DIRECT_OR_INDIRECT))
      .hasSize(1);
  }

  @Test
  void toBreadthOverriddenAlsoFindsAnnotationsAcrossOverrideChain()
    throws Exception {

    Method m = Leaf.class.getDeclaredMethod("execute");

    List<Tag> tags =
      AnnotatedPathBuilder.fromMethod(m)
        .toBreadthOverridden()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("middle", "root");
  }

  @Test
  void toDeclaringClassExtendsPathFromMethodToItsClass() throws Exception {
    @Tag("class-level")
    class Annotated {
      @Tag("method-level")
      public void work() {
      }
    }

    Method m = Annotated.class.getDeclaredMethod("work");
    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(m)
        .toDeclaringClass()
        .build();

    List<Tag> tags = path.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("method-level", "class-level");
  }

  @Test
  void chainedMethodToClassToDepthHierarchy() throws Exception {
    @Tag("super-class")
    class Super {
      @Tag("super-method")
      public void act() {
      }
    }

    @Tag("sub-class")
    class Sub extends Super {
      @Override
      public void act() {
      }
    }

    Method m = Sub.class.getDeclaredMethod("act");
    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(m)
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    List<Tag> tags = path.find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .contains("sub-class", "super-class");
  }
}
