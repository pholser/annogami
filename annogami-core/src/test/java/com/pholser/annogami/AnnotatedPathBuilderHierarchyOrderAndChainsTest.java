package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.RecordComponent;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AnnotatedPathBuilderHierarchyOrderAndChainsTest {
  @Retention(RUNTIME)
  @interface Tag {
    String value();
  }

  // Diamond hierarchy:
  //
  //      Top          @Tag("top") on Top only
  //      / \
  //    Left Right     @Tag("right") on Right only
  //      \ /
  //      Base         no @Tag
  //
  // DFS hierarchy of Base:   [Object, Left, Top, Right]  → Top before Right
  // BFS hierarchy of Base:   [Object, Left, Right, Top]  → Right before Top
  //
  // So findFirst(Tag) on a DFS path returns "top"; on a BFS path returns
  // "right".

  @Tag("top")
  interface Top {
  }

  interface Left extends Top {
  }

  @Tag("right")
  interface Right extends Top {
  }

  static class Base implements Left, Right {
  }

  @Test
  void depthFirstHierarchyFindsTopBeforeRight() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Base.class)
        .toDepthHierarchy()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("top"));
  }

  @Test
  void breadthFirstHierarchyFindsRightBeforeTop() {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromClass(Base.class)
        .toBreadthHierarchy()
        .build();

    assertThat(path.findFirst(Tag.class, DIRECT))
      .hasValueSatisfying(t ->
        assertThat(t.value()).isEqualTo("right"));
  }

  @Tag("service-class")
  static class Service {
    @Tag("service-method")
    void process(@Tag("service-param") String input) {
    }
  }

  @Tag("extended-service-class")
  static class ExtendedService extends Service {
  }

  @Test
  void parameterToMethodToClassToDepthHierarchy() throws Exception {
    Parameter p =
      Service.class.getDeclaredMethod("process", String.class)
        .getParameters()[0];

    List<Tag> tags =
      AnnotatedPathBuilder.fromParameter(p)
        .toDeclaringMethod()
        .toDeclaringClass()
        .toDepthHierarchy()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("service-param", "service-method", "service-class");
  }

  @Tag("widget-class")
  static class Widget {
    @Tag("widget-field")
    String name;
  }

  @Tag("special-widget-class")
  static class SpecialWidget extends Widget {
  }

  @Test
  void fieldToDeclaringClassToDepthHierarchy() throws Exception {
    Field f = SpecialWidget.class.getSuperclass().getDeclaredField("name");

    List<Tag> tags =
      AnnotatedPathBuilder.fromField(f)
        .toDeclaringClass()
        .toDepthHierarchy()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("widget-field", "widget-class");
  }

  @Tag("shape-base")
  interface Shape {
  }

  @Tag("point-record")
  record Point(@Tag("x-component") int x, int y) implements Shape {
  }

  @Test
  void recordComponentToDeclaringRecordToDepthHierarchy() {
    RecordComponent rc = Point.class.getRecordComponents()[0];

    List<Tag> tags =
      AnnotatedPathBuilder.fromRecordComponent(rc)
        .toDeclaringRecord()
        .toDepthHierarchy()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .contains("x-component", "point-record", "shape-base");
  }

  @Tag("factory-class")
  static class Factory {
    @Tag("factory-method")
    Class<?> createLocal() {
      class Local {
      }
      return Local.class;
    }
  }

  @Test
  void localClassToEnclosingMethodToDeclaringClass() {
    Class<?> k = new Factory().createLocal();

    List<Tag> tags =
      AnnotatedPathBuilder.fromClass(k)
        .toEnclosingMethod()
        .toDeclaringClass()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("factory-method", "factory-class");
  }

  @Tag("repo-class")
  static class Repository {
    @Tag("repo-constructor")
    Repository(@Tag("repo-param") String url) {
    }
  }

  @Tag("caching-repo-class")
  static class CachingRepository extends Repository {
    CachingRepository(String url) {
      super(url);
    }
  }

  @Test
  void constructorParamToConstructorToClassToBreadthHierarchy()
    throws Exception {

    Parameter p =
      Repository.class.getDeclaredConstructor(String.class)
        .getParameters()[0];

    List<Tag> tags =
      AnnotatedPathBuilder.fromParameter(p)
        .toDeclaringConstructor()
        .toDeclaringClass()
        .toBreadthHierarchy()
        .build()
        .find(Tag.class, DIRECT_OR_INDIRECT);

    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("repo-param", "repo-constructor", "repo-class");
  }
}
