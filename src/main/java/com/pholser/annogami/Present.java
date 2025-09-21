package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toCollection;

/**
 * An object that can find annotations that are "present" on a program element.
 */
public final class Present implements SingleByType, All {
  Present() {
  }

  @Override public <A extends Annotation> Optional<A> find(
    Class<A> annoType,
    AnnotatedElement target) {

    return Optional.ofNullable(target.getAnnotation(annoType));
  }

  @Override public List<Annotation> all(AnnotatedElement target) {
    return Arrays.stream(target.getAnnotations())
      .collect(toCollection(ArrayList::new));
  }
}
