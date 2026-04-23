package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

/**
 * Detector that retrieves at most one annotation of a requested type from an
 * {@link AnnotatedElement} according to a particular search strategy.
 *
 * <p>The underlying JDK methods used by each strategy
 * ({@link AnnotatedElement#getDeclaredAnnotation(Class)} for {@link Direct},
 * {@link AnnotatedElement#getAnnotation(Class)} for {@link Present}) return
 * a single instance or {@code null}. When a repeatable annotation type is
 * used and two or more instances are present, the JVM stores them inside a
 * generated container annotation; neither single-result method unwraps the
 * container, so {@link #find} will return empty in that case. Use
 * {@link AllByType} (via {@link Presences#DIRECT_OR_INDIRECT} or
 * {@link Presences#ASSOCIATED}) to retrieve all instances of a repeatable
 * annotation type.
 *
 * <p>Instances are obtained from the constants on {@link Presences}; this
 * interface is not intended to be implemented directly.
 */
public sealed interface Single
  permits Direct, Present, MetaSingleAll {

  /**
   * Returns the annotation of type {@code annoType} found on {@code target}
   * according to this detector's search strategy, or empty if none is found.
   *
   * <p>Returns empty when {@code annoType} is repeatable and more than one
   * instance is present; use {@link AllByType} in that case.
   *
   * @param annoType the annotation type to look for
   * @param target   the annotated element to inspect
   * @return the matching annotation, or empty if none is found
   */
  <A extends Annotation>
  Optional<A> find(Class<A> annoType, AnnotatedElement target);

  default <A extends Annotation> Optional<A> find(
    Class<A> annoType,
    AnnotatedElement target,
    Aliasing aliasing) {

    return SegmentResolver.defaults()
      .findFirst(annoType, target, this, aliasing);
  }
}
