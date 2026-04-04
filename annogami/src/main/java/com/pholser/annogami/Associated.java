package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;

public final class Associated implements AllByType {
  Associated() {
  }

  @Override
  public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AnnotatedElement target) {

    return List.of(Sources.PRESENT.byType(annoType, target));
  }

  @Override
  public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AnnotatedElement target,
    Aliasing aliasing) {

    Objects.requireNonNull(annoType, "annoType");
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(aliasing, "aliasing");
    return SegmentResolver.withSeedSource(Sources.PRESENT)
      .allByType(annoType, target, find(annoType, target), aliasing);
  }
}
