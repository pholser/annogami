package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;

import static com.pholser.annogami.ClassHierarchies.breadthFirstHierarchyOf;
import static com.pholser.annogami.ClassHierarchies.breadthFirstOverrideHierarchyOf;
import static com.pholser.annogami.ClassHierarchies.depthFirstHierarchyOf;
import static com.pholser.annogami.ClassHierarchies.depthFirstOverrideHierarchyOf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class AnnotatedPath {
  private final List<AnnotatedElement> elements;

  private AnnotatedPath(List<AnnotatedElement> elements) {
    this.elements = elements;
  }

  public static Builder.Parameter fromParameter(Parameter p) {
    return new Builder.Parameter(p);
  }

  public static Builder.Method fromMethod(Method m) {
    return new Builder.Method(m);
  }

  public static Builder.Constructor fromConstructor(Constructor<?> c) {
    return new Builder.Constructor(c);
  }

  public static Builder.Field fromField(Field f) {
    return new Builder.Field(f);
  }

  public static Builder.Class fromClass(Class<?> k) {
    return new Builder.Class(k);
  }

  public <A extends Annotation> Optional<A> findFirst(
    Class<A> annoType,
    SingleByType detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .findFirst();
  }

  public <A extends Annotation> A merge(
    Class<A> annoType,
    SingleByType detector) {

    return elements.stream()
      .flatMap(e -> detector.find(annoType, e).stream())
      .collect(merged(annoType));
  }

  public List<Annotation> all(All detector) {
    return elements.stream()
      .flatMap(e -> detector.all(e).stream())
      .collect(toList());
  }

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
      }).collect(toList());
  }

  public <A extends Annotation> List<A> findAll(
    Class<A> annoType,
    AllByType detector) {

    return elements.stream()
      .flatMap(e -> detector.findAll(annoType, e).stream())
      .collect(toList());
  }

  private <A extends Annotation>
  Collector<A, Map<String, Object>, A> merged(Class<A> annoType) {
    return new AnnotationMerger<>(annoType);
  }

  public static class Builder {
    public static class Parameter {
      private final List<AnnotatedElement> elements = new ArrayList<>();
      private final java.lang.reflect.Parameter p;

      Parameter(java.lang.reflect.Parameter p) {
        this.p = p;
        elements.add(p);
      }

      public Constructor toDeclaringConstructor() {
        Executable exec = p.getDeclaringExecutable();
        if (!(exec instanceof java.lang.reflect.Constructor<?> c)) {
          throw new IllegalStateException(
            "Parameter " + p + " not declared on a constructor");
        }
        return new Constructor(c, elements);
      }

      public Method toDeclaringMethod() {
        Executable exec = p.getDeclaringExecutable();
        if (!(exec instanceof java.lang.reflect.Method m)) {
          throw new IllegalStateException(
            "Parameter " + p + " not declared on a method");
        }
        return new Method(m, elements);
      }
    }

    public static class Constructor {
      private final java.lang.reflect.Constructor<?> c;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Constructor(java.lang.reflect.Constructor<?> c) {
        this(c, Collections.emptyList());
      }

      Constructor(
        java.lang.reflect.Constructor<?> c,
        List<AnnotatedElement> history) {

        this.c = c;
        elements.addAll(history);
        elements.add(c);
      }

      public Class toDeclaringClass() {
        return new Class(c.getDeclaringClass(), elements);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Method {
      private final java.lang.reflect.Method m;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Method(java.lang.reflect.Method m) {
        this(m, Collections.emptyList());
      }

      Method(
        java.lang.reflect.Method m,
        List<AnnotatedElement> history) {

        this.m = m;
        elements.addAll(history);
        elements.add(m);
      }

      public Class toDeclaringClass() {
        return new Class(m.getDeclaringClass(), elements);
      }

      public Methods toDepthOverridden() {
        return new Methods(depthFirstOverrideHierarchyOf(m), elements);
      }

      public Methods toBreadthOverridden() {
        return new Methods(breadthFirstOverrideHierarchyOf(m), elements);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Field {
      private final java.lang.reflect.Field f;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Field(java.lang.reflect.Field f) {
        this(f, Collections.emptyList());
      }

      Field(
        java.lang.reflect.Field f,
        List<AnnotatedElement> history) {

        this.f = f;
        elements.addAll(history);
        elements.add(f);
      }

      public Class toDeclaringClass() {
        return new Class(f.getDeclaringClass(), elements);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Class {
      private final java.lang.Class<?> k;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Class(java.lang.Class<?> k) {
        this(k, Collections.emptyList());
      }

      Class(
        java.lang.Class<?> k,
        List<AnnotatedElement> history) {

        this.k = k;
        elements.addAll(history);
        elements.add(k);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }

      public Package toDeclaringPackage() {
        return new Package(k.getPackage(), elements);
      }

      public Module toDeclaringModule() {
        return new Module(k.getModule(), elements);
      }

      public Method toEnclosingMethod() {
        java.lang.reflect.Method enclosing = k.getEnclosingMethod();
        if (enclosing == null) {
          throw new IllegalStateException(k + " has no enclosing method");
        }

        return new Method(enclosing, elements);
      }

      public Constructor toEnclosingConstructor() {
        java.lang.reflect.Constructor<?> enclosing =
          k.getEnclosingConstructor();
        if (enclosing == null) {
          throw new IllegalStateException(k + " has no enclosing constructor");
        }

        return new Constructor(enclosing, elements);
      }

      public Classes toClassEnclosure() {
        List<java.lang.Class<?>> enclosure = new ArrayList<>();
        for (java.lang.Class<?> c = k.getEnclosingClass();
          c != null;
          c = c.getEnclosingClass()) {

          enclosure.add(c);
        }

        return new Classes(enclosure, elements);
      }

      public Classes toDepthHierarchy() {
        return new Classes(depthFirstHierarchyOf(k), elements);
      }

      public Classes toBreadthHierarchy() {
        return new Classes(breadthFirstHierarchyOf(k), elements);
      }
    }

    public static class Methods {
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Methods(
        List<java.lang.reflect.Method> methods,
        List<AnnotatedElement> history) {

        elements.addAll(history);
        elements.addAll(methods);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Classes {
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Classes(
        List<java.lang.Class<?>> classes,
        List<AnnotatedElement> history) {

          elements.addAll(history);
        elements.addAll(classes);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Package {
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Package(
        java.lang.Package p,
        List<AnnotatedElement> history) {

        elements.addAll(history);
        elements.add(p);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Module {
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Module(
        java.lang.Module m,
        List<AnnotatedElement> history) {

        elements.addAll(history);
        elements.add(m);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }
  }
}
