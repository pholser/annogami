package com.pholser.annogami;

import com.pholser.annogami.annotations.Infrastructure;
import com.pholser.annogami.annotations.Infrastructure.ClassAnnotation;
import com.pholser.annogami.annotations.Infrastructure.ConfigAnnotation;
import com.pholser.annogami.annotations.Infrastructure.GrandChildClass;
import com.pholser.annogami.annotations.Infrastructure.ImplementingClass;
import com.pholser.annogami.annotations.Infrastructure.MethodAnnotation;
import com.pholser.annogami.annotations.Infrastructure.NonInheritedClassAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static com.pholser.annogami.Presences.PRESENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

/**
 * Tests for PRESENT presence level in duly-noted.
 * <p/>
 * Maps to Spring's AnnotationUtils.findAnnotation() methods that search inheritance hierarchies
 * and JUnit's AnnotationSupport methods that traverse class hierarchies.
 */
@DisplayName("Present Presence Tests")
public class PresentPresenceGeneratedTest {
  /**
   * Corresponds to: Spring's AnnotationUtils.findAnnotation(Class<?> clazz, Class<A> annotationType)
   * Reference: "Find a single Annotation of annotationType on the supplied Class,
   *            traversing its interfaces, annotations, and superclasses if the annotation
   *            is not directly present on the given class itself."
   */
  @Test
  @DisplayName("Should find inherited class annotations")
  void findInheritedClassAnnotation() {
    ClassAnnotation a =
      PRESENT.find(ClassAnnotation.class, GrandChildClass.class)
          .orElseGet(() -> fail("Missing annotation"));

    assertEquals("child", a.value());
  }

  /**
   * Corresponds to: Spring's annotation inheritance behavior with @Inherited
   * Validates: Respects Java's @Inherited annotation semantics
   */
  @Test
  @DisplayName("Should respect @Inherited annotation semantics")
  void respectInheritedSemantics() {
    Optional<ClassAnnotation> inherited =
      PRESENT.find(ClassAnnotation.class, GrandChildClass.class);

    Optional<NonInheritedClassAnnotation> nonInherited =
      PRESENT.find(NonInheritedClassAnnotation.class, GrandChildClass.class);

    assertAll(
      () -> assertThat(inherited).isPresent(),
      () -> assertThat(nonInherited).isNotPresent());
  }

  /**
   * Corresponds to: Spring's inheritance hierarchy traversal order
   * Validates: Most specific (closest to leaf) annotation wins
   */
  @Test
  @DisplayName("Should respect inheritance hierarchy order")
  void respectInheritanceOrder() {
    ConfigAnnotation a =
      PRESENT.find(ConfigAnnotation.class, GrandChildClass.class)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals("grandchild", a.name());
  }

  /**
   * Corresponds to: Spring's interface annotation discovery
   * Reference: Spring searches interfaces when annotation not found on class
   */
  @Test
  @DisplayName("Should find annotations on implemented interfaces")
  void findInterfaceAnnotations() {
    PRESENT.find(ClassAnnotation.class, ImplementingClass.class)
      .orElseGet(() -> fail("Missing annotation"));

    // Should find both class and interface annotations.
    // Which wins is implementation-dependent.
  }

  /**
   * Corresponds to: JUnit's AnnotationSupport.findAnnotation(Method, Class)
   * Reference: "Find the first annotation of the specified type that is either
   *            present or meta-present on the supplied optional element."
   */
  @Test
  @DisplayName("""
      Should not find method annotations in inheritance hierarchy,
      since the annotation is not Inherited, does not target a class, etc.
      """)
  void findMethodAnnotationsInHierarchy() throws Exception {
    PRESENT.find(
      MethodAnnotation.class,
      GrandChildClass.class.getMethod("rootMethod", String.class))
      .ifPresent(a -> fail("Should not have found annotation"));
  }

  /**
   * Corresponds to: Spring's behavior with interface default methods
   * Validates: Annotation discovery on interface default methods
   */
  @Test
  @DisplayName("Should find annotations on interface default methods")
  void findInterfaceDefaultMethodAnnotations() throws Exception {
    MethodAnnotation a =
      PRESENT.find(
        MethodAnnotation.class,
        ImplementingClass.class.getMethod("interfaceMethod"))
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals("extended-interface-method", a.value());
  }

  /**
   * Corresponds to: Spring's package annotation discovery
   * Validates: Finding annotations on packages (if supported)
   */
  @Test
  @DisplayName("Should handle package annotations")
  void handlePackageAnnotations() {
    PRESENT.find(ClassAnnotation.class, Infrastructure.class.getPackage())
      .ifPresent(a -> fail("Should not have found annotation"));
  }
}
