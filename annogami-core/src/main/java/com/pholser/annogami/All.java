package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;

/**
 * Detector that retrieves <em>all</em> annotations present on an
 * {@link AnnotatedElement} according to a particular search strategy.
 *
 * <p>Instances are obtained from the constants on {@link Presences}; this
 * interface is not intended to be implemented directly.
 */
public sealed interface All
  permits Direct, Present, MetaSingleAll {

  /**
   * Returns all annotations found on {@code target} according to this
   * detector's search strategy.
   *
   * @param target the annotated element to inspect
   * @return all matching annotations, in encounter order; never {@code null}
   */
  List<Annotation> all(AnnotatedElement target);

  /**
   * Returns all annotations found on {@code target}, then applies
   * {@code aliasing} to synthesize additional annotations derived from the
   * collected set.
   *
   * @param target the annotated element to inspect
   * @param aliasing the aliasing strategy used to synthesize annotations
   * from the collected meta-context
   * @return all matching annotations including any synthesized by aliasing,
   * in encounter order; never {@code null}
   */
  default List<Annotation> all(
    AnnotatedElement target,
    Aliasing aliasing) {

    Objects.requireNonNull(aliasing, "aliasing");

    return SegmentResolver.defaults().all(target, all(target), aliasing);
  }
}
