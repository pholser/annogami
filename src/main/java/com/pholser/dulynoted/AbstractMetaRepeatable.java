package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractMetaRepeatable<P extends AllByType>
  implements AllByType {

  private final P presence;
  private final All all;

  AbstractMetaRepeatable(P presence, All all) {
    this.presence = presence;
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

    List<A> allByType = presence.findAll(annoType, target);
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
