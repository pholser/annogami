package com.pholser.annogami;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that depth-first and breadth-first hierarchy traversal produce
 * different {@code findFirst} results when annotations are placed at levels
 * that the two orderings visit in different order, and exercises multi-step
 * builder chains.
 */
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
  // So findFirst(Tag) on a DFS path returns "top"; on a BFS path returns "right".

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
    AnnotatedPath path = AnnotatedPathBuilder.fromClass(Base.class)
      .toDepthHierarchy()
      .build();

    Tag t = path.findFirst(Tag.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(t.value()).isEqualTo("top");
  }

  @Test
  void breadthFirstHierarchyFindsRightBeforeTop() {
    AnnotatedPath path = AnnotatedPathBuilder.fromClass(Base.class)
      .toBreadthHierarchy()
      .build();

    Tag t = path.findFirst(Tag.class, DIRECT).orElseGet(Assertions::fail);
    assertThat(t.value()).isEqualTo("right");
  }

  // -------------------------------------------------------------------------
  // Multi-step chains
  // -------------------------------------------------------------------------

  // --- parameter → method → class → hierarchy ---

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
  void parameterToMethodToClassToDepthHierarchyBuildsFullChain()
    throws Exception {

    var param = Service.class
      .getDeclaredMethod("process", String.class)
      .getParameters()[0];

    List<Tag> tags = AnnotatedPathBuilder.fromParameter(param)
      .toDeclaringMethod()
      .toDeclaringClass()
      .toDepthHierarchy()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    // parameter → method → Service → Object (no tag) → ... no more
    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("service-param", "service-method", "service-class");
  }

  // --- field → class → depth hierarchy ---

  @Tag("widget-class")
  static class Widget {
    @Tag("widget-field")
    String name;
  }

  @Tag("special-widget-class")
  static class SpecialWidget extends Widget {
  }

  @Test
  void fieldToDeclaringClassToDepthHierarchyBuildsFullChain()
    throws Exception {

    var field = SpecialWidget.class.getSuperclass().getDeclaredField("name");

    List<Tag> tags = AnnotatedPathBuilder.fromField(field)
      .toDeclaringClass()
      .toDepthHierarchy()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    // field → Widget → Object (no tag)
    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("widget-field", "widget-class");
  }

  // --- record component → record → depth hierarchy ---

  @Tag("shape-base")
  interface Shape {
  }

  @Tag("point-record")
  record Point(@Tag("x-component") int x, int y) implements Shape {
  }

  @Test
  void recordComponentToDeclaringRecordToDepthHierarchyBuildsFullChain() {
    var rc = Point.class.getRecordComponents()[0]; // x

    List<Tag> tags = AnnotatedPathBuilder.fromRecordComponent(rc)
      .toDeclaringRecord()
      .toDepthHierarchy()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    // component → Point → Shape (via hierarchy) → Object (no tag) → Record (no tag)
    assertThat(tags)
      .extracting(Tag::value)
      .contains("x-component", "point-record", "shape-base");
  }

  // --- local class → enclosing method → declaring class ---

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
  void localClassToEnclosingMethodToDeclaringClassBuildsFullChain()
    throws Exception {

    Class<?> localClass = new Factory().createLocal();

    List<Tag> tags = AnnotatedPathBuilder.fromClass(localClass)
      .toEnclosingMethod()
      .toDeclaringClass()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    // Local (no tag) → createLocal() → Factory
    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("factory-method", "factory-class");
  }

  // --- constructor param → constructor → class → breadth hierarchy ---

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
  void constructorParamToConstructorToClassToBreadthHierarchyBuildsFullChain()
    throws Exception {

    var param = Repository.class
      .getDeclaredConstructor(String.class)
      .getParameters()[0];

    List<Tag> tags = AnnotatedPathBuilder.fromParameter(param)
      .toDeclaringConstructor()
      .toDeclaringClass()
      .toBreadthHierarchy()
      .build()
      .find(Tag.class, DIRECT_OR_INDIRECT);

    // param → constructor → Repository → Object (no tag)
    assertThat(tags)
      .extracting(Tag::value)
      .containsExactly("repo-param", "repo-constructor", "repo-class");
  }
}
