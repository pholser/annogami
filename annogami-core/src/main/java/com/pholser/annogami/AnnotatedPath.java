package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AnnotatedPath {
  private final List<AnnotatedElement> elements;

  public AnnotatedPath(List<AnnotatedElement> elements) {
    this.elements = List.copyOf(elements);
  }

  public <A extends Annotation> Optional<A> findFirst(
    Class<A> annoType,
    Single detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .findFirst();
  }

  public <A extends Annotation> Optional<A> findFirst(
    Class<A> annoType,
    Single detector,
    Aliasing aliasing) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e, aliasing).stream())
      .findFirst();
  }

  public <A extends Annotation> Optional<A> merge(
    Class<A> annoType,
    Single detector) {

    return mergeInstances(annoType, elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .toList());
  }

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
        Object val = invokeAttr(a, attr);

        if (!Objects.deepEquals(val, defaultVal)) {
          overrides.put(attr.getName(), val);
          break;
        }
      }
    }

    return Optional.of(SynthesizedAnnotations.of(annoType, overrides));
  }

  private static Object invokeAttr(Annotation a, Method attr) {
    if (Proxy.isProxyClass(a.getClass())) {
      InvocationHandler h = Proxy.getInvocationHandler(a);
      try {
        return h.invoke(a, attr, null);
      } catch (RuntimeException | Error e) {
        throw e;
      } catch (Throwable t) {
        throw new UndeclaredThrowableException(t);
      }
    }
    try {
      return attr.invoke(a);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("Cannot read attribute " + attr, e);
    }
  }

  public List<Annotation> all(All detector) {
    return elements.stream()
      .flatMap(e -> detector.all(e).stream())
      .toList();
  }

  public List<Annotation> all(All detector, Aliasing aliasing) {
    return elements.stream()
      .flatMap(e -> detector.all(e, aliasing).stream())
      .toList();
  }

  public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AllByType detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .toList();
  }

  public <A extends Annotation> List<A> find(
    Class<A> annoType,
    AllByType detector,
    Aliasing aliasing) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e, aliasing).stream())
      .toList();
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
