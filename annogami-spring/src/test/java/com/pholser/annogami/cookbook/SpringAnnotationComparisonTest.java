package com.pholser.annogami.cookbook;

import com.pholser.annogami.Aliasing;
import com.pholser.annogami.spring.SpringAliasing;
import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.AnnotatedPathBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static com.pholser.annogami.Presences.META_ASSOCIATED;
import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates equivalence and differences between Spring's annotation
 * utilities and annogami for common annotation discovery patterns,
 * using real Spring annotation types.
 */
class SpringAnnotationComparisonTest {

  // --- test subjects ---

  @Service
  static class MyService {
  }

  // Real @Service is NOT @Inherited — neither META_ASSOCIATED
  // nor Spring's TYPE_HIERARCHY finds it via the JVM @Inherited
  // mechanism; Spring walks the hierarchy explicitly.
  @Service
  static class ServiceBase {
  }

  static class DerivedService extends ServiceBase {
  }

  static class MappedController {
    @GetMapping("/orders")
    void listOrders() {
    }
  }

  @Transactional(timeout = 30)
  static class TxService {
    @Transactional(readOnly = true)
    void readData() {
    }
  }

  // --- tests ---

  @Test
  void componentOnDirectClass_springAndAnnogamiAgree() {
    boolean spring = AnnotatedElementUtils
      .hasAnnotation(MyService.class, Component.class);

    boolean annogami = !META_DIRECT_OR_INDIRECT
      .find(Component.class, MyService.class)
      .isEmpty();

    assertThat(spring).isTrue();
    assertThat(annogami).isEqualTo(spring);
  }

  @Test
  void serviceOnSuperclass_springFinds_metaAssociatedDoesNot() {
    // Real @Service is NOT @Inherited. Spring's TYPE_HIERARCHY strategy
    // explicitly walks the class hierarchy regardless of @Inherited.
    // META_ASSOCIATED only follows annotations marked @Inherited at the
    // JVM level, so it cannot reach @Service on the superclass.
    boolean spring = MergedAnnotations
      .from(DerivedService.class, SearchStrategy.TYPE_HIERARCHY)
      .isPresent(Component.class);

    boolean annogami = !META_ASSOCIATED
      .find(Component.class, DerivedService.class)
      .isEmpty();

    assertThat(spring).isTrue();
    assertThat(annogami).isFalse(); // META_ASSOCIATED cannot reach it
  }

  @Test
  void serviceOnSuperclass_annotatedPathBridgesGap() {
    // For annotations not marked @Inherited on superclasses, use an
    // explicit AnnotatedPath over the class hierarchy to replicate
    // Spring's TYPE_HIERARCHY behaviour.
    AnnotatedPath path = AnnotatedPathBuilder
      .fromClass(DerivedService.class)
      .toDepthHierarchy()
      .build();

    boolean spring = MergedAnnotations
      .from(DerivedService.class, SearchStrategy.TYPE_HIERARCHY)
      .isPresent(Component.class);

    boolean annogami = !path
      .find(Component.class, META_DIRECT_OR_INDIRECT)
      .isEmpty();

    assertThat(spring).isTrue();
    assertThat(annogami).isEqualTo(spring);
  }

  @Test
  void attributeSynthesis_springAndAnnogamiAgree()
    throws Exception {
    Method method = MappedController.class
      .getDeclaredMethod("listOrders");

    // Spring: returns null if absent; synthesized proxy if found
    RequestMapping spring = AnnotatedElementUtils
      .findMergedAnnotation(method, RequestMapping.class);

    // annogami: returns Optional — absence is explicit
    Optional<RequestMapping> annogami = META_DIRECT.find(
      RequestMapping.class, method, SpringAliasing.aliasing());

    assertThat(spring).isNotNull();
    assertThat(annogami).isPresent();

    assertThat(spring.path()).containsExactly("/orders");
    assertThat(annogami.get().path()).containsExactly("/orders");
  }

  @Test
  void springFindMergedReturnsNearestWholeAnnotation()
    throws Exception {
    Method method = TxService.class.getDeclaredMethod("readData");

    // Spring finds the method-level @Transactional and returns it
    // as-is. The class-level timeout=30 is not consulted.
    Transactional tx = AnnotatedElementUtils
      .findMergedAnnotation(method, Transactional.class);

    assertThat(tx.readOnly()).isTrue();       // from method
    assertThat(tx.timeout()).isEqualTo(-1);   // method default; class ignored
  }

  @Test
  void annogamiMergeFillsAttributesAcrossPathElements()
    throws Exception {
    Method method = TxService.class.getDeclaredMethod("readData");

    AnnotatedPath path = AnnotatedPathBuilder
      .fromMethod(method)
      .toDeclaringClass()
      .build();

    // Method's readOnly=true wins (non-default).
    // Class fills in timeout=30 for the attribute left at default.
    Optional<Transactional> tx = path.merge(
      Transactional.class, DIRECT);

    assertThat(tx).isPresent();
    assertThat(tx.get().readOnly()).isTrue();      // from method
    assertThat(tx.get().timeout()).isEqualTo(30);  // filled from class
  }

  @Test
  void springReturnsOneMatch_annogamiPathReturnsAll()
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
    assertThat(all.get(0).readOnly()).isTrue();     // method
    assertThat(all.get(1).timeout()).isEqualTo(30); // class
  }
}
