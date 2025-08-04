package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

/**
 * An object that can find annotations that are "directly present" or
 * "indirectly present" on a program element.
 */
public final class DirectOrIndirect implements AllByType {
  DirectOrIndirect() {
  }

  @Override public <A extends Annotation> List<A> findAll(
    Class<A> annoType,
    AnnotatedElement target) {

    return Arrays.asList(target.getDeclaredAnnotationsByType(annoType));
  }
}
