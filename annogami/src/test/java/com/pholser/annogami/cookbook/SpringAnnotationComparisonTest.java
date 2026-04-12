package com.pholser.annogami.cookbook;

import com.pholser.annogami.Aliasing;
import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.AnnotatedPathBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.META_ASSOCIATED;
import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates equivalence and differences between Spring's annotation
 * utilities and annogami for common annotation discovery patterns.
 */
class SpringAnnotationComparisonTest {

  // --- annotation types mimicking Spring patterns ---

  @Retention(RUNTIME) @Target(TYPE) @interface Component {}

  // @Inherited: META_ASSOCIATED can reach @Component on subclasses
  @Retention(RUNTIME) @Target(TYPE) @Inherited @Component
  @interface Service {}

  // NOT @Inherited: META_ASSOCIATED cannot reach @Component on subclasses
  @Retention(RUNTIME) @Target(TYPE) @Component
  @interface Repository {}

  @Retention(RUNTIME) @Target({TYPE, METHOD})
  @interface Transactional {
    boolean readOnly() default false;
    int timeout() default -1;
  }

  @Retention(RUNTIME) @Target({METHOD, ANNOTATION_TYPE})
  @interface RequestMapping {
    String[] path() default {};
  }

  @Retention(RUNTIME) @Target(METHOD) @RequestMapping
  @interface GetMapping {
    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] value() default {};
  }

  // --- test subjects ---

  @Service static class MyService {}

  @Service static class ServiceBase {}
  static class DerivedFromInherited extends ServiceBase {}

  @Repository static class RepositoryBase {}
  static class DerivedFromNonInherited extends RepositoryBase {}

  static class MappedController {
    @GetMapping("/orders") void listOrders() {}
  }

  @Transactional(timeout = 30)
  static class TxService {
    @Transactional(readOnly = true) void readData() {}
  }

  // --- tests ---

  @Test void componentOnDirectClass_springAndAnnogamiAgree() {
    boolean spring = AnnotatedElementUtils
      .hasAnnotation(MyService.class, Component.class);

    boolean annogami = !META_DIRECT_OR_INDIRECT
      .find(Component.class, MyService.class)
      .isEmpty();

    assertThat(spring).isTrue();
    assertThat(annogami).isEqualTo(spring);
  }

  @Test void inheritedSuperclass_metaAssociatedAgreesWithSpring() {
    // @Service is @Inherited, so DerivedFromInherited inherits it.
    // Both Spring's TYPE_HIERARCHY and META_ASSOCIATED find it.
    boolean spring = MergedAnnotations
      .from(DerivedFromInherited.class, SearchStrategy.TYPE_HIERARCHY)
      .isPresent(Component.class);

    boolean annogami = !META_ASSOCIATED
      .find(Component.class, DerivedFromInherited.class)
      .isEmpty();

    assertThat(spring).isTrue();
    assertThat(annogami).isEqualTo(spring);
  }

  @Test void nonInheritedSuperclass_springFinds_metaAssociatedDoesNot() {
    // @Repository is NOT @Inherited. Spring's TYPE_HIERARCHY strategy
    // explicitly walks the class hierarchy; META_ASSOCIATED only
    // follows annotations marked @Inherited at the JVM level.
    boolean spring = MergedAnnotations
      .from(
        DerivedFromNonInherited.class,
        SearchStrategy.TYPE_HIERARCHY)
      .isPresent(Component.class);

    boolean annogami = !META_ASSOCIATED
      .find(Component.class, DerivedFromNonInherited.class)
      .isEmpty();

    assertThat(spring).isTrue();
    assertThat(annogami).isFalse(); // META_ASSOCIATED cannot reach it
  }

  @Test void nonInheritedSuperclass_annotatedPathBridgesGap() {
    // For non-@Inherited superclass annotations, use an explicit
    // AnnotatedPath over the class hierarchy to replicate
    // Spring's TYPE_HIERARCHY behaviour.
    AnnotatedPath path = AnnotatedPathBuilder
      .fromClass(DerivedFromNonInherited.class)
      .toDepthHierarchy()
      .build();

    boolean spring = MergedAnnotations
      .from(
        DerivedFromNonInherited.class,
        SearchStrategy.TYPE_HIERARCHY)
      .isPresent(Component.class);

    boolean annogami = !path
      .find(Component.class, META_DIRECT_OR_INDIRECT)
      .isEmpty();

    assertThat(spring).isTrue();
    assertThat(annogami).isEqualTo(spring);
  }

  @Test void attributeSynthesis_springAndAnnogamiAgree()
      throws Exception {
    Method method = MappedController.class
      .getDeclaredMethod("listOrders");

    // Spring: returns null if absent; synthesized proxy if found
    RequestMapping spring = AnnotatedElementUtils
      .findMergedAnnotation(method, RequestMapping.class);

    // annogami: returns Optional — absence is explicit
    Optional<RequestMapping> annogami = META_DIRECT.find(
      RequestMapping.class, method, Aliasing.spring());

    assertThat(spring).isNotNull();
    assertThat(annogami).isPresent();

    assertThat(spring.path()).containsExactly("/orders");
    assertThat(annogami.get().path()).containsExactly("/orders");
  }

  @Test void springFindMergedReturnsNearestWholeAnnotation()
      throws Exception {
    Method method = TxService.class.getDeclaredMethod("readData");

    // Spring finds the method-level @Transactional and returns it
    // as-is. The class-level timeout=30 is not consulted.
    Transactional tx = AnnotatedElementUtils
      .findMergedAnnotation(method, Transactional.class);

    assertThat(tx.readOnly()).isTrue();   // from method
    assertThat(tx.timeout()).isEqualTo(-1); // method default; class ignored
  }

  @Test void annogamiMergeFillsAttributesAcrossPathElements()
      throws Exception {
    Method method = TxService.class.getDeclaredMethod("readData");

    AnnotatedPath path = AnnotatedPathBuilder
      .fromMethod(method)
      .toDeclaringClass()
      .build();

    // Method's readOnly=true wins (non-default).
    // Class fills in timeout=30 for the attribute method left at default.
    Optional<Transactional> tx = path.merge(
      Transactional.class, DIRECT);

    assertThat(tx).isPresent();
    assertThat(tx.get().readOnly()).isTrue();    // from method
    assertThat(tx.get().timeout()).isEqualTo(30); // filled from class
  }

  @Test void springReturnsOneMatch_annogamiPathReturnsAll()
      throws Exception {
    Method method = TxService.class.getDeclaredMethod("readData");

    // Spring returns the nearest (method-level) occurrence only
    Transactional spring = AnnotatedElementUtils
      .findMergedAnnotation(method, Transactional.class);
    assertThat(spring.readOnly()).isTrue();

    // annogami returns every occurrence along the path, in order
    AnnotatedPath path = AnnotatedPathBuilder
      .fromMethod(method)
      .toDeclaringClass()
      .build();

    List<Transactional> all = path.find(
      Transactional.class, DIRECT_OR_INDIRECT);

    assertThat(all).hasSize(2);
    assertThat(all.get(0).readOnly()).isTrue();    // method
    assertThat(all.get(1).timeout()).isEqualTo(30); // class
  }
}
