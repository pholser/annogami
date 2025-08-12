package com.pholser.annogami;

import com.pholser.annogami.annotations.Infrastructure;
import com.pholser.annogami.annotations.Infrastructure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static com.pholser.annogami.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@DisplayName("Direct Presence Tests")
public class DirectPresenceGeneratedTest {

  /**
   * Corresponds to: Spring's AnnotationUtils.isAnnotationDeclaredLocally()
   * Validates: Annotation is directly declared on the element, not inherited
   */
  @Test
  @DisplayName("Should find directly present class annotation")
  void findDirectClassAnnotation() {
    // Spring equivalent: AnnotationUtils.isAnnotationDeclaredLocally(ChildClass.class, ClassAnnotation.class)
    ClassAnnotation a =
      DIRECT.find(ClassAnnotation.class, ChildClass.class)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals("child", a.value());
  }

  /**
   * Corresponds to: Spring's AnnotationUtils.isAnnotationDeclaredLocally() returning false
   * Validates: DIRECT does not find inherited annotations
   */
  @Test
  @DisplayName("Should not find inherited class annotation with DIRECT")
  void shouldNotFindInheritedClassAnnotation() {
    // Spring equivalent: AnnotationUtils.isAnnotationDeclaredLocally(GrandChildClass.class, ClassAnnotation.class) == false
    DIRECT.find(ClassAnnotation.class, GrandChildClass.class)
      .ifPresent(a -> fail("Should not have found annotation"));
  }

  /**
   * Corresponds to: JDK's AnnotatedElement.getAnnotation() on methods
   * Validates: Finding annotation directly declared on method
   */
  @Test
  @DisplayName("Should find directly present method annotation")
  void findDirectMethodAnnotation() throws Exception {
    Method m = ChildClass.class.getMethod("rootMethod", String.class);

    // JDK equivalent: method.getAnnotation(MethodAnnotation.class)
    MethodAnnotation a =
      DIRECT.find(MethodAnnotation.class, m)
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals("child-method", a.value());
  }

  /**
   * Corresponds to: JDK's AnnotatedElement.getAnnotations()
   * Validates: Finding all directly declared annotations
   */
  @Test
  @DisplayName("Should find all directly present annotations")
  void findAllDirectAnnotations() {
    // JDK equivalent: ChildClass.class.getAnnotations()
    List<Annotation> all = DIRECT.all(ChildClass.class);

    assertTrue(all.size() >= 3); // ClassAnnotation, ComposedAnnotation, ConfigAnnotation
    assertTrue(all.stream().anyMatch(a -> a instanceof ClassAnnotation));
    assertTrue(all.stream().anyMatch(a -> a instanceof ComposedAnnotation));
    assertTrue(all.stream().anyMatch(a -> a instanceof ConfigAnnotation));
  }

  /**
   * Corresponds to: JDK's AnnotatedElement.getAnnotation() returning null
   * Validates: DIRECT returns empty when annotation not directly present
   */
  @Test
  @DisplayName("Should return empty for non-directly-present annotation")
  void returnEmptyForNonDirectAnnotation() {
    // JDK equivalent: PlainClass.class.getAnnotation(ClassAnnotation.class) == null
    Optional<ClassAnnotation> annotation =
      DIRECT.find(ClassAnnotation.class, Infrastructure.PlainClass.class);

    assertFalse(annotation.isPresent());
  }

  /**
   * Corresponds to: JDK's parameter annotation handling
   * Validates: Finding annotations on method parameters
   */
  @Test
  @DisplayName("Should find parameter annotations")
  void findParameterAnnotations() throws NoSuchMethodException {
    Method method = ChildClass.class.getMethod("rootMethod", String.class);
    var parameter = method.getParameters()[0];

    // JDK equivalent: parameter.getAnnotation(ParameterAnnotation.class)
    Optional<Infrastructure.ParameterAnnotation> annotation =
      DIRECT.find(Infrastructure.ParameterAnnotation.class, parameter);

    assertTrue(annotation.isPresent());
    assertEquals("child-param", annotation.get().value());
  }

  /**
   * Corresponds to: JDK's field annotation handling
   * Validates: Finding annotations on fields
   */
  @Test
  @DisplayName("Should find field annotations")
  void findFieldAnnotations() throws NoSuchFieldException {
    var field = ChildClass.class.getDeclaredField("childField");

    // JDK equivalent: field.getAnnotation(FieldAnnotation.class)
    Optional<Infrastructure.FieldAnnotation> annotation =
      DIRECT.find(Infrastructure.FieldAnnotation.class, field);

    assertTrue(annotation.isPresent());
    assertEquals("child-field", annotation.get().value());
  }
}
