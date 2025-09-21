package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

sealed abstract class AbstractMeta<D extends SingleByType & All>
  implements SingleByType, All
  permits Meta, MetaDirect {

  private final D detector;

  AbstractMeta(D detector) {
    this.detector = detector;
  }

  /**
   * {@inheritDoc}
   *
   * Use our detector to look for an annotation of the given type on the
   * target; if not found there, recursively use our detector (depth-first) on
   * the annotations that the detector finds on the target.
   */
  @Override public <A extends Annotation> Optional<A> find(
    Class<A> annoType,
    AnnotatedElement target) {

    return findMeta(annoType, target, new HashSet<>());
  }

  private <A extends Annotation> Optional<A> findMeta(
    Class<A> annoType,
    AnnotatedElement target,
    Set<Class<? extends Annotation>> seen) {

    return detector.find(annoType, target)
      .or(() ->
        detector.all(target)
          .stream()
          .map(Annotation::annotationType)
          .filter(t -> !seen.contains(t))
          .flatMap(t -> {
            seen.add(t);
            return findMeta(annoType, t, seen).stream();
          })
          .findFirst());
  }

  /**
   * {@inheritDoc}
   *
   * Recursively use our detector to produce a depth-first list of annotations
   * that the detector finds.
   */
  @Override public List<Annotation> all(AnnotatedElement target) {
    List<Annotation> accumulation = new ArrayList<>();
    allMeta(target, new HashSet<>(), accumulation);
    return accumulation;
  }

  private void allMeta(
    AnnotatedElement target,
    Set<Class<? extends Annotation>> seen,
    List<Annotation> accumulation) {

    detector.all(target)
      .forEach(a -> {
        accumulation.add(a);

        Class<? extends Annotation> t = a.annotationType();
        if (!seen.contains(t)) {
          seen.add(t);
          allMeta(t, seen, accumulation);
        }
      });
  }
}
