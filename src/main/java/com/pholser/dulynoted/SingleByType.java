package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

/**
 * Contract for an object that can ask for a non-repeated annotation
 * by its type on a program element. Implementers decide what "on" means.
 */
public sealed interface SingleByType permits AbstractMeta, Direct, Present {
  /**
   * Gives a non-repeated annotation of the given type from the given
   * program element.
   *
   * @param annoType class representing type of annotation to find
   * @param target the element to perform the search on
   * @param <A> represents the desired annotation type
   * @return an optional representing the found annotation, or empty
   * if it was not found
   */
  <A extends Annotation>
  Optional<A> find(Class<A> annoType, AnnotatedElement target);
}
