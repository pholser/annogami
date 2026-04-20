package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class SynthesizedAnnotations {
  private SynthesizedAnnotations() {
    throw new AssertionError();
  }

  public static <A extends Annotation> A of(
    Class<A> annoType, Map<String, Object> overrides) {

    return annoType.cast(Proxy.newProxyInstance(
      annoType.getClassLoader(),
      new Class<?>[]{annoType},
      new SynthesizedAnnotationHandler(annoType, overrides)));
  }
}
