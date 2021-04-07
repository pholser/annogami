package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;

final class Present implements SingleByType, All {
  @Override public <A extends Annotation> Optional<A> find(
    Class<A> annoType,
    AnnotatedElement target) {

    return Optional.ofNullable(target.getAnnotation(annoType));
  }

  @Override public List<Annotation> all(AnnotatedElement target) {
    return List.of(target.getAnnotations());
  }
}
