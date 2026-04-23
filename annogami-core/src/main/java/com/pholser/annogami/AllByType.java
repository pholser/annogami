package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;

/**
 * Detector that retrieves all annotations of a specific type present on an
 * {@link AnnotatedElement} according to a particular search strategy.
 *
 * <p>Unlike {@link All}, which returns every annotation regardless of type,
 * this interface filters results to a single requested annotation type. This
 * is useful when an element may carry the same annotation type more than once
 * (for example, via a repeatable annotation container) or when only one type
 * is of interest.
 *
 * <p>Instances are obtained from the constants on {@link Presences}; this
 * interface is not intended to be implemented directly.
 */
public sealed interface AllByType
  permits DirectOrIndirect, Associated, MetaAllByType {

  /**
   * Returns all annotations of type {@code annoType} found on {@code target}
   * according to this detector's search strategy.
   *
   * @param annoType the annotation type to look for
   * @param target the annotated element to inspect
   * @return all matching annotations, in encounter order; never {@code null}
   */
  <A extends Annotation>
  List<A> find(Class<A> annoType, AnnotatedElement target);

  /**
   * Returns all annotations of type {@code annoType} found on {@code target},
   * also synthesizing instances derived via {@code aliasing} from the
   * collected meta-context.
   *
   * @param annoType the annotation type to look for
   * @param target the annotated element to inspect
   * @param aliasing the aliasing strategy used to synthesize annotations
   * from the collected meta-context
   * @return all matching annotations including any synthesized by aliasing,
   * in encounter order; never {@code null}
   */
  default <A extends Annotation> List<A> find(
    Class<A> annoType,
    AnnotatedElement target,
    Aliasing aliasing) {

    Objects.requireNonNull(aliasing, "aliasing");

    return SegmentResolver.defaults()
      .allByType(annoType, target, find(annoType, target), aliasing);
  }
}
