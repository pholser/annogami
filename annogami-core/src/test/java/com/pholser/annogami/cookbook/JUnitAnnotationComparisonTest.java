package com.pholser.annogami.cookbook;

import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.AnnotatedPathBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates equivalence and differences between JUnit's
 * AnnotationSupport and annogami for common annotation discovery
 * patterns.
 */
class JUnitAnnotationComparisonTest {

  // --- composed annotation types ---

  static final class NoOpExtension implements Extension {
  }

  @Retention(RUNTIME)
  @Target({TYPE, METHOD})
  @ExtendWith(NoOpExtension.class)
  @interface WithNoOp {
  }

  @Retention(RUNTIME)
  @Target(METHOD)
  @Test
  @Tag("fast")
  @Tag("unit")
  @interface FastTest {
  }

  // --- test subjects ---

  @WithNoOp
  static class ExtendedSubject {
  }

  static class ComposedSubject {
    @FastTest
    void myTest() {
    }
  }

  static class LifecycleBase {
    @BeforeEach
    void setUp() {
    }
  }

  static class LifecycleDerived extends LifecycleBase {
    // Overrides setUp() but does not redeclare @BeforeEach
    @Override
    void setUp() {
    }
  }

  // --- tests ---

  @Test
  void extensionDiscovery_bothFindThroughComposed() {
    // JUnit: findRepeatableAnnotations follows composed annotations
    List<ExtendWith> junit = AnnotationSupport
      .findRepeatableAnnotations(
        ExtendedSubject.class, ExtendWith.class);

    // annogami: META_DIRECT_OR_INDIRECT walks the meta-annotation chain
    List<ExtendWith> annogami = META_DIRECT_OR_INDIRECT
      .find(ExtendWith.class, ExtendedSubject.class);

    assertThat(junit).hasSize(1);
    assertThat(annogami).hasSize(1);

    // Both surface the same extension class
    assertThat(junit.get(0).value())
      .containsExactly(NoOpExtension.class);
    assertThat(annogami.get(0).value())
      .containsExactly(NoOpExtension.class);
  }

  @Test
  void testDetection_bothFindThroughComposed() throws Exception {
    Method method = ComposedSubject.class
      .getDeclaredMethod("myTest");

    // JUnit: isAnnotated follows composed annotations
    boolean junit = AnnotationSupport.isAnnotated(method, Test.class);

    // annogami: META_DIRECT_OR_INDIRECT walks the meta-annotation chain
    boolean annogami = !META_DIRECT_OR_INDIRECT
      .find(Test.class, method)
      .isEmpty();

    assertThat(junit).isTrue();
    assertThat(annogami).isEqualTo(junit);
  }

  @Test
  void tagDiscovery_bothFindThroughComposed() throws Exception {
    Method method = ComposedSubject.class
      .getDeclaredMethod("myTest");

    // JUnit: findRepeatableAnnotations unwraps @Repeatable containers
    // and follows composed annotations
    List<Tag> junit = AnnotationSupport
      .findRepeatableAnnotations(method, Tag.class);

    // annogami: META_DIRECT_OR_INDIRECT walks the meta-annotation chain
    List<Tag> annogami = META_DIRECT_OR_INDIRECT
      .find(Tag.class, method);

    // Both find both @Tag values from @FastTest
    assertThat(junit)
      .extracting(Tag::value)
      .containsExactlyInAnyOrder("fast", "unit");
    assertThat(annogami)
      .extracting(Tag::value)
      .containsExactlyInAnyOrder("fast", "unit");
  }

  @Test
  void beforeEach_differentQuestions() throws Exception {
    // JUnit's question: which methods in this class hierarchy carry
    // @BeforeEach? It searches the class structure and returns the
    // annotated method, which may be in a superclass.
    boolean junitFindsItOnBase = AnnotationSupport
      .isAnnotated(
        LifecycleBase.class.getDeclaredMethod("setUp"),
        BeforeEach.class);
    boolean junitFindsItOnDerived = AnnotationSupport
      .isAnnotated(
        LifecycleDerived.class.getDeclaredMethod("setUp"),
        BeforeEach.class);

    assertThat(junitFindsItOnBase).isTrue();
    // JUnit does not propagate @BeforeEach through overrides at
    // the method level; the overriding method has no annotation
    assertThat(junitFindsItOnDerived).isFalse();

    // annogami's question: does this specific method, or any method
    // it overrides, carry @BeforeEach? The path follows the override
    // chain and finds it on the superclass method.
    Method derived = LifecycleDerived.class
      .getDeclaredMethod("setUp");

    AnnotatedPath path = AnnotatedPathBuilder
      .fromMethod(derived)
      .toDepthOverridden()
      .build();

    Optional<BeforeEach> annogami =
      path.findFirst(BeforeEach.class, DIRECT);

    assertThat(annogami).isPresent();
  }
}
