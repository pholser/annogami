package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public final class AnnotatedPath {
  private final List<AnnotatedElement> elements;

  private AnnotatedPath(List<AnnotatedElement> elements) {
    this.elements = new ArrayList<>(elements);
  }

  static Builder fromParameter(Parameter p) {
    return new Builder().addParameter(p);
  }

  /**
   * Gives the first non-repeated annotation of the given type found
   * along this path of annotated elements using the given detector.
   *
   * @param annotationType class representing type of annotation to find
   * @param detector strategy for finding the annotation
   * @param <A> represents the desired annotation type
   * @return an optional representing the found annotation, or empty
   * if it was not found
   */
  <A extends Annotation> Optional<A> find(
    Class<A> annotationType,
    SingleByTypeDetector detector) {

    return elements.stream()
      .map(e -> detector.find(annotationType, e))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst();
  }

  <A extends Annotation> Optional<A> merge(
    Class<A> annotationType,
    SingleByTypeDetector detector) {

    return Optional.empty();
  }

  /**
   * Gives all the annotations of a given type found along this path
   * of annotated elements using the given detector.
   *
   * @param annotationType class representing type of annotation to find
   * @param detector strategy for finding the annotation
   * @param <A> represents the desired annotation type
   * @return the found annotations; may be empty
   */
  <A extends Annotation> List<A> findAll(
    Class<A> annotationType,
    AllByTypeDetector detector) {

    return elements.stream()
      .flatMap(e -> detector.findAll(annotationType, e).stream())
      .collect(toList());
  }

  <A extends Annotation> Optional<A> merge(
    Class<A> annotationType,
    AllByTypeDetector detector) {

    return Optional.empty();
  }

  /**
   * Gives all the annotations found along this path of annotated elements
   * using the given detector.
   *
   * @param detector strategy for finding the annotations
   * @return list of all the annotations found
   */
  List<Annotation> all(AllDetector detector) {
    return new ArrayList<>();
  }

  List<Annotation> merge(AllDetector detector) {
    return new ArrayList<>();
  }

  static class Builder {
    private final List<AnnotatedElement> elements = new ArrayList<>();

    public Builder addParameter(Parameter p) {
      elements.add(p);
      return this;
    }

    public AnnotatedPath build() {
      return new AnnotatedPath(elements);
    }
  }
}
