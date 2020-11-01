package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;

/**
 * An object that can find annotations that are "directly present" on a
 * program element.
 */
public final class DirectPresence
  implements SingleByTypeDetector, AllDetector {

  @Override public <A extends Annotation> Optional<A> find(
    Class<A> annotationType,
    AnnotatedElement target) {

    return Optional.ofNullable(
      target.getDeclaredAnnotation(annotationType));
  }

  @Override public List<Annotation> all(AnnotatedElement target) {
    return List.of(target.getDeclaredAnnotations());
  }
}
