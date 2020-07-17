package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * Contract for an object that can give the annotations of a given type
 * on a program element. Implementers decide what "on" means.
 */
interface AllByTypeDetector {
  /**
   * Gives all the annotations of a given type on the given program
   * element.
   *
   * @param annotationType class representing type of annotation to find
   * @param target the element to perform the search on
   * @param <A> represents the desired annotation type
   * @return the found annotations; may be empty
   */
  <A extends Annotation>
  List<A> findAll(Class<A> annotationType, AnnotatedElement target);
}
