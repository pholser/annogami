package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

public final class Associated implements AllByType {
  Associated() {
  }

  @Override public <A extends Annotation> List<A> findAll(
    Class<A> annoType,
    AnnotatedElement target) {

    return Arrays.asList(target.getAnnotationsByType(annoType));
  }
}
