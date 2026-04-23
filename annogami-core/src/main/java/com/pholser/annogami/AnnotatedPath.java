package com.pholser.annogami;

import com.pholser.annogami.internal.AnnotationInvoker;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An ordered sequence of {@link AnnotatedElement}s that are searched
 * together for annotations.
 *
 * <p>A path models the idea that annotation lookup should span multiple
 * related elements — for example, a method and the interface it overrides,
 * or a class and its superclasses. Elements are searched in the order they
 * were supplied; earlier elements take priority.
 *
 * <p>Paths are constructed directly via {@link #AnnotatedPath(List)} or
 * assembled incrementally using {@link SegmentBuilder}.
 */
public final class AnnotatedPath {
  private final List<AnnotatedElement> elements;

  /**
   * Constructs a path over the given elements.
   *
   * @param elements the elements to search, in priority order
   */
  public AnnotatedPath(List<AnnotatedElement> elements) {
    this.elements = List.copyOf(elements);
  }

  /**
   * Returns the first annotation of type {@code annoType} found anywhere
   * along this path, using {@code detector}'s search strategy on each element.
   *
   * <p>Returns empty when {@code annoType} is repeatable and more than one
   * instance is present on a given element; use
   * {@link #find(Class, AllByType)} in that case.
   *
   * @param annoType the annotation type to look for
   * @param detector the search strategy to apply to each element
   * @return the first matching annotation, or empty if none is found
   */
  public <A extends Annotation> Optional<A> findFirst(
    Class<A> annoType,
    Single detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .findFirst();
  }

  /**
   * Returns the first annotation of type {@code annoType} found anywhere
   * along this path, using {@code detector}'s search strategy on each element
   * and {@code aliasing} to synthesize additional candidates from the
   * meta-context of each element.
   *
   * <p>Returns empty when {@code annoType} is repeatable and more than one
   * instance is present on a given element; use
   * {@link #find(Class, AllByType, Aliasing)} in that case.
   *
   * @param annoType the annotation type to look for
   * @param detector the search strategy to apply to each element
   * @param aliasing the aliasing strategy used to synthesize annotations
   * @return the first matching annotation, or empty if none is found
   */
  public <A extends Annotation> Optional<A> findFirst(
    Class<A> annoType,
    Single detector,
    Aliasing aliasing) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e, aliasing).stream())
      .findFirst();
  }

  /**
   * Collects all instances of {@code annoType} along this path and merges
   * them into a single synthesized annotation.
   *
   * <p>For each attribute of {@code annoType}, the first non-default value
   * encountered across the collected instances wins. If no instances are
   * found, an empty {@code Optional} is returned.
   *
   * <p>When {@code annoType} is repeatable and more than one instance is
   * present on a given element, that element contributes at most one
   * instance to the merge; see {@link Single} for details.
   *
   * @param annoType the annotation type to merge
   * @param detector the search strategy to apply to each element
   * @return a merged annotation, or empty if no instances were found
   */
  public <A extends Annotation> Optional<A> merge(
    Class<A> annoType,
    Single detector) {

    return mergeInstances(annoType, elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .toList());
  }

  /**
   * Collects all instances of {@code annoType} along this path — including
   * those synthesized by {@code aliasing} — and merges them into a single
   * synthesized annotation.
   *
   * <p>For each attribute of {@code annoType}, the first non-default value
   * encountered across the collected instances wins. If no instances are
   * found, an empty {@code Optional} is returned.
   *
   * <p>When {@code annoType} is repeatable and more than one instance is
   * present on a given element, that element contributes at most one
   * instance to the merge; see {@link Single} for details.
   *
   * @param annoType the annotation type to merge
   * @param detector the search strategy to apply to each element
   * @param aliasing the aliasing strategy used to synthesize annotations
   * @return a merged annotation, or empty if no instances were found
   */
  public <A extends Annotation> Optional<A> merge(
    Class<A> annoType,
    Single detector,
    Aliasing aliasing) {

    return mergeInstances(annoType, elements.stream()
      .flatMap(e -> detector.find(annoType, e, aliasing).stream())
      .toList());
  }

  private <A extends Annotation> Optional<A> mergeInstances(
    Class<A> annoType,
    List<A> instances) {

    if (instances.isEmpty()) {
      return Optional.empty();
    }

    Map<String, Object> overrides = new LinkedHashMap<>();

    for (Method attr : annoType.getDeclaredMethods()) {
      Object defaultVal = attr.getDefaultValue();

      for (A a : instances) {
        Object val = AnnotationInvoker.invoke(a, attr, () -> attr.invoke(a));

        if (!Objects.deepEquals(val, defaultVal)) {
          overrides.put(attr.getName(), val);
          break;
        }
      }
    }

    return Optional.of(SynthesizedAnnotations.of(annoType, overrides));
  }

  /**
   * Returns all annotations found across every element of this path, using
   * {@code detector}'s search strategy on each element.
   *
   * @param detector the search strategy to apply to each element
   * @return all annotations in path order; never {@code null}
   */
  public List<Annotation> all(All detector) {
    return elements.stream()
      .flatMap(e -> detector.all(e).stream())
      .toList();
  }

  /**
   * Returns all annotations found across every element of this path, using
   * {@code detector}'s search strategy on each element and {@code aliasing}
   * to synthesize additional annotations from the meta-context of each
   * element.
   *
   * @param detector the search strategy to apply to each element
   * @param aliasing the aliasing strategy used to synthesize annotations
   * @return all annotations in path order; never {@code null}
   */
  public List<Annotation> all(All detector, Aliasing aliasing) {
    return elements.stream()
      .flatMap(e -> detector.all(e, aliasing).stream())
      .toList();
  }

  /**
   * Returns all annotations of type {@code annoType} found across every
   * element of this path, using {@code detector}'s search strategy on each
   * element.
   *
   * @param annoType the annotation type to look for
   * @param detector the search strategy to apply to each element
   * @return all matching annotations in path order; never {@code null}
   */
  public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AllByType detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .toList();
  }

  /**
   * Returns all annotations of type {@code annoType} found across every
   * element of this path, using {@code detector}'s search strategy on each
   * element and {@code aliasing} to synthesize additional annotations from
   * the meta-context of each element.
   *
   * @param annoType the annotation type to look for
   * @param detector the search strategy to apply to each element
   * @param aliasing the aliasing strategy used to synthesize annotations
   * @return all matching annotations in path order; never {@code null}
   */
  public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AllByType detector,
    Aliasing aliasing) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e, aliasing).stream())
      .toList();
  }

  /**
   * Base class for building an {@link AnnotatedPath} from a structured
   * source of annotated elements.
   *
   * <p>Subclasses encapsulate knowledge of how to derive an ordered sequence
   * of {@link AnnotatedElement}s — for example by walking a class hierarchy,
   * collecting method parameter lists, or assembling a chain of overriding
   * methods. The {@link #build()} method finalises the path from whatever
   * elements {@link #predecessors()} returns.
   */
  public static abstract class SegmentBuilder {
    protected SegmentBuilder() {
    }

    /**
     * Builds an {@link AnnotatedPath} from the elements returned by
     * {@link #predecessors()}.
     *
     * @return the completed path
     */
    public final AnnotatedPath build() {
      return new AnnotatedPath(predecessors());
    }

    /**
     * Returns the ordered sequence of annotated elements that form this
     * path segment.
     *
     * @return the elements in priority order; must not be {@code null}
     */
    protected abstract List<AnnotatedElement> predecessors();
  }
}
