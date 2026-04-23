package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * Strategy for synthesizing an annotation of a requested type from a
 * collection of annotations that are present in a meta-context.
 *
 * <p>An {@code Aliasing} describes how attribute values from one or more
 * <em>source</em> annotation types map onto attributes of a <em>target</em>
 * annotation type. When {@link #synthesize} is called, the implementation
 * scans the supplied meta-context for source annotations, extracts any
 * non-default attribute values, and assembles a synthesized instance of the
 * target type if at least one non-default value was found.
 *
 * <p>Two built-in implementations are provided:
 * <ul>
 *   <li>{@code SpringAliasing} (in {@code annogami-spring}) — derives alias
 *       relationships from Spring's {@code @AliasFor} meta-annotations.</li>
 *   <li>{@code ProgrammaticAliasing} (in {@code annogami-programmatic}) —
 *       accepts alias relationships declared in code via a builder, which is
 *       useful when the annotation types cannot be modified.</li>
 * </ul>
 */
public interface Aliasing {
  /**
   * Attempts to synthesize an annotation of type {@code annoType} from the
   * annotations in {@code metaContext}.
   *
   * <p>The implementation scans {@code metaContext} for source annotations
   * whose attributes are aliases for attributes of {@code annoType}.
   * For each target attribute, the first non-default source value encountered
   * wins. If no non-default values are found for any attribute, an empty
   * {@code Optional} is returned.
   *
   * @param annoType the annotation type to synthesize
   * @param metaContext the annotations to draw attribute values from,
   * in priority order (earlier entries win)
   * @return a synthesized annotation instance, or empty if no aliased
   * non-default values were found in the context
   */
  <A extends Annotation> Optional<A> synthesize(
    Class<A> annoType,
    List<Annotation> metaContext);
}
