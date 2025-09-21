package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

/**
 * An object that can find annotations that are "associated" on a
 * program element.
 */
public final class Associated implements AllByType {
  Associated() {
  }

  @Override public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AnnotatedElement target) {

    return Arrays.stream(target.getAnnotationsByType(annoType))
      .collect(toCollection(ArrayList::new));
  }
}
