package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * Contract for an object that can give all the annotations on a given
 * program element. Implementers decide what "on" means.
 */
public sealed interface All permits AbstractMeta, Direct, Present {
  /**
   * Gives all the annotations on a given program element.
   *
   * @param target program element on which to search
   * @return list of all the annotations found on the element
   */
  List<Annotation> all(AnnotatedElement target);
}
