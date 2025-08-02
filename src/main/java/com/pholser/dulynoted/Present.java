package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class Present implements SingleByType, All {
  Present() {
  }

  @Override public <A extends Annotation> Optional<A> find(
    Class<A> annoType,
    AnnotatedElement target) {

    return Optional.ofNullable(target.getAnnotation(annoType));
  }

  @Override public List<Annotation> all(AnnotatedElement target) {
    return Arrays.asList(target.getAnnotations());
  }
}
