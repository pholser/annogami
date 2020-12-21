package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

final class Associated implements AllByType {
  @Override public <A extends Annotation> List<A> findAll(
    Class<A> annoType,
    AnnotatedElement target) {

    return List.of(target.getAnnotationsByType(annoType));
  }
}
