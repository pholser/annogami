package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public interface AnnotatedPath {
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
    SingleByTypeDetector detector);

  <A extends Annotation> Optional<A> merge(
    Class<A> annotationType,
    SingleByTypeDetector detector);

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
    AllByTypeDetector detector);

  <A extends Annotation> Optional<A> merge(
    Class<A> annotationType,
    AllByTypeDetector detector);

  /**
   * Gives all the annotations found along this path of annotated elements
   * using the given detector.
   *
   * @param detector strategy for finding the annotations
   * @return list of all the annotations found
   */
  List<Annotation> all(AllDetector detector);

  List<Annotation> merge(AllDetector detector);
}
