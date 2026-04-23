package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Factory for synthesized annotation instances.
 *
 * <p>A synthesized annotation is a {@link Proxy}-backed instance of an
 * annotation type whose attribute values may differ from those declared in
 * source — typically because values have been propagated from other
 * annotations via an {@link Aliasing} strategy. Attributes not present in
 * the {@code overrides} map fall back to the annotation type's declared
 * defaults.
 *
 * <p>The produced proxy satisfies the full {@link Annotation} contract:
 * {@code annotationType()}, {@code equals}, {@code hashCode}, and
 * {@code toString} all behave as specified by
 * {@link java.lang.annotation.Annotation}.
 */
public final class SynthesizedAnnotations {
  private SynthesizedAnnotations() {
    throw new AssertionError();
  }

  /**
   * Creates a synthesized instance of {@code annoType} with the given
   * attribute overrides.
   *
   * <p>Keys in {@code overrides} that do not correspond to a declared
   * attribute are silently ignored. Type mismatches between an override
   * value and an attribute's declared return type are not checked eagerly;
   * they surface as {@link ClassCastException} when the attribute is
   * accessed on the returned instance.
   *
   * @param annoType the annotation type to synthesize
   * @param overrides map from attribute name to value; attributes absent
   * from this map use the annotation type's declared defaults
   * @return a proxy implementing {@code annoType} with the specified values
   */
  public static <A extends Annotation> A of(
    Class<A> annoType,
    Map<String, Object> overrides) {

    return annoType.cast(
      Proxy.newProxyInstance(
        annoType.getClassLoader(),
        new Class<?>[] { annoType },
        new SynthesizedAnnotationHandler(annoType, overrides)));
  }
}
