package com.pholser.annogami;

import com.pholser.annogami.annotations.Infrastructure.ChildClass;
import com.pholser.annogami.annotations.Infrastructure.ClassAnnotation;
import com.pholser.annogami.annotations.Infrastructure.ComposedAnnotation;
import com.pholser.annogami.annotations.Infrastructure.ConfigAnnotation;
import com.pholser.annogami.annotations.Infrastructure.FieldAnnotation;
import com.pholser.annogami.annotations.Infrastructure.GrandChildClass;
import com.pholser.annogami.annotations.Infrastructure.MethodAnnotation;
import com.pholser.annogami.annotations.Infrastructure.ParameterAnnotation;
import com.pholser.annogami.annotations.Infrastructure.PlainClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Direct Presence Tests")
public class DirectPresenceGeneratedTest {
  /**
   * Corresponds to: Spring's AnnotationUtils.isAnnotationDeclaredLocally()
   * Validates: Annotation is directly declared on the element, not inherited
   */
  @Test
  @DisplayName("Should find directly present class annotation")
  void findDirectClassAnnotation() {
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
    MethodAnnotation a =
      DIRECT.find(
        MethodAnnotation.class,
        ChildClass.class.getMethod("rootMethod", String.class))
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
    List<Annotation> all = DIRECT.all(ChildClass.class);

    assertAll(
      () -> assertThat(all.size()).isGreaterThanOrEqualTo(3),
      () -> assertThat(all.stream()).anyMatch(a -> a instanceof ClassAnnotation),
      () -> assertThat(all.stream()).anyMatch(a -> a instanceof ComposedAnnotation),
      () -> assertThat(all.stream()).anyMatch(a -> a instanceof ConfigAnnotation));
  }

  /**
   * Corresponds to: JDK's AnnotatedElement.getAnnotation() returning null
   * Validates: DIRECT returns empty when annotation not directly present
   */
  @Test
  @DisplayName("Should return empty for non-directly-present annotation")
  void returnEmptyForNonDirectAnnotation() {
    DIRECT.find(ClassAnnotation.class, PlainClass.class)
      .ifPresent(a -> fail("Should not have found annotation"));
  }

  /**
   * Corresponds to: JDK's parameter annotation handling
   * Validates: Finding annotations on method parameters
   */
  @Test
  @DisplayName("Should find parameter annotations")
  void findParameterAnnotations() throws Exception {
    ParameterAnnotation a =
      DIRECT.find(
        ParameterAnnotation.class,
        ChildClass.class
          .getMethod("rootMethod", String.class)
          .getParameters()[0])
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals("child-param", a.value());
  }

  /**
   * Corresponds to: JDK's field annotation handling
   * Validates: Finding annotations on fields
   */
  @Test
  @DisplayName("Should find field annotations")
  void findFieldAnnotations() throws NoSuchFieldException {
    FieldAnnotation a =
      DIRECT.find(
        FieldAnnotation.class,
        ChildClass.class.getDeclaredField("childField"))
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals("child-field", a.value());
  }
}
