package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

sealed abstract class AbstractMetaRepeatable<D extends AllByType>
  implements AllByType
  permits MetaAssociated, MetaDirectOrIndirect {

  private final D detector;
  private final All all;

  AbstractMetaRepeatable(D detector, All all) {
    this.detector = detector;
    this.all = all;
  }

  /**
   * {@inheritDoc}
   *
   * Look for instances of the annotation type present on the target;
   * then recursively search the annotations (depth-first) that are present
   * for more instances of the annotation type.
   */
  @Override public <A extends Annotation> List<A> findAll(
    Class<A> annoType,
    AnnotatedElement target) {

    List<A> results = new ArrayList<>();
    findAllMeta(annoType, target, results, new HashSet<>());
    return results;
  }

  private <A extends Annotation> void findAllMeta(
    Class<A> annoType,
    AnnotatedElement target,
    List<A> accumulation,
    Set<Class<? extends Annotation>> seen) {

    List<A> allByType = detector.findAll(annoType, target);
    accumulation.addAll(allByType);

    all.all(target)
      .stream()
      .map(Annotation::annotationType)
      .filter(t -> !seen.contains(t))
      .forEach(t -> {
        seen.add(t);
        findAllMeta(annoType, t, accumulation, seen);
      });
  }
}
