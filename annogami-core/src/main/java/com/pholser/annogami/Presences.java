package com.pholser.annogami;

/**
 * Entry point for all built-in annotation search strategies.
 *
 * <p>The eight constants fall into two groups:
 *
 * <h2>Direct-element strategies</h2>
 * <p>These examine only the annotations present on the element itself,
 * using the standard JDK reflection methods:
 * <ul>
 *   <li>{@link #DIRECT} — annotations declared directly on the element
 *       ({@link java.lang.reflect.AnnotatedElement#getDeclaredAnnotation
 *       getDeclaredAnnotation} / {@link
 *       java.lang.reflect.AnnotatedElement#getDeclaredAnnotations
 *       getDeclaredAnnotations}). Implements both {@link Single} and
 *       {@link All}.</li>
 *   <li>{@link #DIRECT_OR_INDIRECT} — annotations declared directly or
 *       inside a repeatable-annotation container ({@link
 *       java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType
 *       getDeclaredAnnotationsByType}). Implements {@link AllByType}.</li>
 *   <li>{@link #PRESENT} — annotations directly present or inherited via
 *       {@link java.lang.annotation.Inherited @Inherited} ({@link
 *       java.lang.reflect.AnnotatedElement#getAnnotation getAnnotation}
 *       / {@link java.lang.reflect.AnnotatedElement#getAnnotations
 *       getAnnotations}). Implements both {@link Single} and {@link All}.</li>
 *   <li>{@link #ASSOCIATED} — annotations directly present, inherited, or
 *       inside a repeatable-annotation container ({@link
 *       java.lang.reflect.AnnotatedElement#getAnnotationsByType
 *       getAnnotationsByType}). Implements {@link AllByType}.</li>
 * </ul>
 *
 * <h2>Meta-annotation strategies</h2>
 * <p>These perform a breadth-first walk of the annotation type graph: they
 * start from the annotations on the element, then examine the annotations
 * on each of those annotation types, and so on recursively. This makes it
 * possible to find an annotation that is not directly on the element but is
 * applied to one of its annotations (or to an annotation on an annotation,
 * etc.) — for example, finding {@code @RequestMapping} on a class that
 * carries {@code @GetMapping}, where {@code @GetMapping} is itself
 * meta-annotated with {@code @RequestMapping}.
 *
 * <p>The four meta strategies differ in how they seed the walk (which
 * annotations on the start element are collected first) and in how they
 * resolve repeatable annotations along the path:
 * <ul>
 *   <li>{@link #META_DIRECT} — seeds from declared annotations; resolves
 *       each annotation type step with {@code getDeclaredAnnotation}.
 *       Implements both {@link Single} and {@link All}.</li>
 *   <li>{@link #META_DIRECT_OR_INDIRECT} — seeds from declared annotations;
 *       resolves each step with {@code getDeclaredAnnotationsByType}.
 *       Implements {@link AllByType}.</li>
 *   <li>{@link #META_PRESENT} — seeds from present (including
 *       {@code @Inherited}) annotations; resolves each step with
 *       {@code getDeclaredAnnotation}. Implements both {@link Single} and
 *       {@link All}.</li>
 *   <li>{@link #META_ASSOCIATED} — seeds from present annotations; resolves
 *       each step with {@code getDeclaredAnnotationsByType}. Implements
 *       {@link AllByType}.</li>
 * </ul>
 */
public final class Presences {
  /**
   * Finds annotations declared directly on the element.
   * Implements {@link Single} and {@link All}.
   *
   * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotation(Class)
   * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotations()
   */
  public static final Direct DIRECT = new Direct();

  /**
   * Finds annotations declared directly on the element, including those
   * inside a repeatable-annotation container.
   * Implements {@link AllByType}.
   *
   * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType(Class)
   */
  public static final DirectOrIndirect DIRECT_OR_INDIRECT =
    new DirectOrIndirect();

  /**
   * Finds annotations directly present on the element or inherited via
   * {@link java.lang.annotation.Inherited @Inherited}.
   * Implements {@link Single} and {@link All}.
   *
   * @see java.lang.reflect.AnnotatedElement#getAnnotation(Class)
   * @see java.lang.reflect.AnnotatedElement#getAnnotations()
   */
  public static final Present PRESENT = new Present();

  /**
   * Finds annotations directly present on the element or inherited via
   * {@link java.lang.annotation.Inherited @Inherited}, including those
   * inside a repeatable-annotation container.
   * Implements {@link AllByType}.
   *
   * @see java.lang.reflect.AnnotatedElement#getAnnotationsByType(Class)
   */
  public static final Associated ASSOCIATED = new Associated();

  /**
   * Walks the meta-annotation graph breadth-first, seeding from the
   * element's declared annotations and resolving each step with
   * {@code getDeclaredAnnotation}.
   * Implements {@link Single} and {@link All}.
   */
  public static final MetaDirect META_DIRECT = new MetaDirect();

  /**
   * Walks the meta-annotation graph breadth-first, seeding from the
   * element's declared annotations and resolving each step with
   * {@code getDeclaredAnnotationsByType}.
   * Implements {@link AllByType}.
   */
  public static final MetaDirectOrIndirect META_DIRECT_OR_INDIRECT =
    new MetaDirectOrIndirect();

  /**
   * Walks the meta-annotation graph breadth-first, seeding from the
   * element's present (including {@link java.lang.annotation.Inherited
   * @Inherited}) annotations and resolving each step with
   * {@code getDeclaredAnnotation}.
   * Implements {@link Single} and {@link All}.
   */
  public static final MetaPresent META_PRESENT = new MetaPresent();

  /**
   * Walks the meta-annotation graph breadth-first, seeding from the
   * element's present (including {@link java.lang.annotation.Inherited
   * @Inherited}) annotations and resolving each step with
   * {@code getDeclaredAnnotationsByType}.
   * Implements {@link AllByType}.
   */
  public static final MetaAssociated META_ASSOCIATED = new MetaAssociated();

  private Presences() {
    throw new AssertionError();
  }
}
