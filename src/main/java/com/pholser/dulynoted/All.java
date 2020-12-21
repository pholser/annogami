package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * Contract for an object that can give all the annotations on a given
 * program element. Implementers decide what "on" means.
 */
interface All {
  /**
   * Gives all the annotations on a given program element.
   *
   * @param target program element on which to search
   * @return list of all the annotations found on the element
   */
  List<Annotation> all(AnnotatedElement target);
}
