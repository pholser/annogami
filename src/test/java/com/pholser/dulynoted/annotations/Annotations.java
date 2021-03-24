package com.pholser.dulynoted.annotations;

import io.leangen.geantyref.AnnotationFormatException;
import io.leangen.geantyref.TypeFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Map;

public class Annotations {
  private Annotations() {
    throw new UnsupportedOperationException();
  }

  /*
   * Make an annotation of the given type, assumed to have no attributes.
   */
  public static <A extends Annotation> A anno(Class<A> annoType) {
    return anno(annoType, Map.of());
  }

  /*
   * Make an annotation of the given type, assumed to have only a "value"
   * attribute.
   */
  public static <A extends Annotation> A annoValue(
    Class<A> annoType,
    Object value) {

    return anno(annoType, Map.of("value", value));
  }

  /*
   * Make a container annotation whose type is the given type, and whose
   * contained annotations are of the other given type.
   */
  @SafeVarargs
  public static <A extends Annotation, B extends Annotation>
  A containerAnno(
    Class<A> containerType,
    Class<B> repeatedAnnoType,
    B... repetitions) {

    Object typedRepetitions =
      Array.newInstance(repeatedAnnoType, repetitions.length);
    System.arraycopy(
      repetitions,
      0,
      typedRepetitions,
      0,
      repetitions.length);

    return anno(containerType, Map.of("value", typedRepetitions));
  }

  public static <A extends Annotation> A anno(
    Class<A> annoType,
    Map<String, Object> attributes) {

    try {
      return TypeFactory.annotation(annoType, attributes);
    } catch (AnnotationFormatException e) {
      throw new IllegalStateException(e);
    }
  }
}
