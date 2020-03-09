package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static java.util.Arrays.asList;

public final class AssociatedPresence implements AllByTypeDetector {
  @Override public <A extends Annotation> List<A> findAll(
    Class<A> annotationType,
    AnnotatedElement target) {

    return asList(target.getAnnotationsByType(annotationType));
  }
}
