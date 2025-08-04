package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

sealed abstract class AbstractMeta<P extends SingleByType & All>
  implements SingleByType, All
  permits Meta, MetaDirect {

  private final P presence;

  AbstractMeta(P presence) {
    this.presence = presence;
  }

  /**
   * {@inheritDoc}
   *
   * Look for presence of the annotation type on the target;
   * if not found there, recursively search the annotations (depth-first)
   * that are present.
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

    return presence.find(annoType, target)
      .or(() ->
        presence.all(target)
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
   * Depth-first list of the annotations present on the target, recursively.
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

    presence.all(target)
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
