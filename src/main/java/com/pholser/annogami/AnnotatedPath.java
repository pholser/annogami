package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;

/**
 * A sequence of annotated program elements, along which to find and merge
 * annotations.
 */
public final class AnnotatedPath {
  private final List<AnnotatedElement> elements;

  /**
   * Make an annotated path from a sequence of annotated elements.
   *
   * @param elements the annotated element sequence
   */
  public AnnotatedPath(List<AnnotatedElement> elements) {
    this.elements = List.copyOf(elements);
  }

  /**
   * Finds the first annotation of the given type along the path, using the
   * given detector.
   *
   * @param <A> represents the desired annotation type
   * @param annoType class representing type of annotation to find
   * @param detector annotation detector to use
   * @return an optional representing the found annotation, or empty if it was
   * not found
   */
  public <A extends Annotation> Optional<A> findFirst(
    Class<A> annoType,
    SingleByType detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .findFirst();
  }

  /**
   * Finds all the annotations of the given type along the path, using the
   * given detector, and merges them together into a single annotation
   * with attributes from annotations earlier in the path taking precedence
   * over attributes from annotations later in the path.
   *
   * @param <A> represents the desired annotation type
   * @param annoType class representing type of annotation to find
   * @param detector annotation detector to use
   * @return an optional representing the merged annotation, or empty if it
   * was not found
   */
  public <A extends Annotation> Optional<A> merge(
    Class<A> annoType,
    SingleByType detector) {

    List<A> targets =
      elements.stream()
        .flatMap(e -> detector.find(annoType, e).stream())
        .toList();
    return targets.isEmpty()
      ? Optional.empty()
      : Optional.of(targets.stream().collect(merged(annoType)));
  }

  /**
   * Finds and gives all annotations of any type along the path, using the given
   * detector, in order of encounter.
   *
   * @param detector annotation detector to use
   * @return a list of the found annotations
   */
  public List<Annotation> all(All detector) {
    return elements.stream()
      .flatMap(e -> detector.all(e).stream())
      .toList();
  }

  /**
   * Finds all annotations of any type along the path, using the given
   * detector, and merges them together into single annotations of each found
   * type, with attributes from annotations earlier in the path taking
   * precedence over attributes from annotations later in the path.
   *
   * @param detector annotation detector to use
   * @return a list of the merged annotations
   */
  public List<Annotation> mergeAll(All detector) {
    Map<Class<? extends Annotation>, List<Annotation>> byAnnoType =
      elements.stream()
        .flatMap(e -> detector.all(e).stream())
        .collect(groupingBy(Annotation::annotationType));
    return byAnnoType.entrySet().stream()
      .map(e -> {
        @SuppressWarnings("unchecked")
        Class<Annotation> keyType = (Class<Annotation>) e.getKey();

        return e.getValue().stream().collect(merged(keyType));
      }).toList();
  }

  /**
   * Finds and gives all the annotations of the given type along the path,
   * using the given detector, in order of encounter.
   *
   * @param <A> represents the desired annotation type
   * @param annoType class representing type of annotation to find
   * @param detector annotation detector to use
   * @return a list of the found annotations
   */
  public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AllByType detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .toList();
  }

  private <A extends Annotation>
  Collector<A, Map<String, Object>, A> merged(Class<A> annoType) {
    return new AnnotationMerger<>(annoType);
  }

  /**
   * Abstract class for annotated path segment builders. Builders may structure
   * themselves however they wish; but ultimately must provide a sequence of
   * annotated elements from which another segment may continue the path.
   */
  public static abstract class SegmentBuilder {
    /**
     * Make a new path builder.
     */
    protected SegmentBuilder() {
    }

    /**
     * Complete an in-progress path.
     *
     * @return a complete path
     */
    public final AnnotatedPath build() {
      return new AnnotatedPath(predecessors());
    }

    /**
     * Gives a sequence of "predecessor" annotated elements for an annotated
     * path builder as it continues a path.
     *
     * @return predecessor elements in sequence
     */
    protected abstract List<AnnotatedElement> predecessors();
  }
}
