package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

/**
 * An object that can find annotations that are "directly present" or
 * "indirectly present" on a program element.
 */
public final class DirectOrIndirect implements AllByType {
  DirectOrIndirect() {
  }

  @Override public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AnnotatedElement target) {

    return Arrays.stream(target.getDeclaredAnnotationsByType(annoType))
      .collect(toCollection(ArrayList::new));
  }
}
