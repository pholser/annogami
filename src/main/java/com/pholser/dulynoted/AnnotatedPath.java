package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  public <A extends Annotation> Optional<A> findFirst(
    Class<A> annoType,
    SingleByTypeDetector direct) {

    return elements.stream()
      .flatMap(e -> direct.find(annoType, e).stream())
      .findFirst();
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
        if (!(exec instanceof java.lang.reflect.Constructor<?>)) {
          throw new IllegalStateException(
            "Parameter " + p + " not declared on a constructor");
        }
        return new Constructor((java.lang.reflect.Constructor) exec, elements);
      }

      public Method toDeclaringMethod() {
        Executable exec = p.getDeclaringExecutable();
        if (!(exec instanceof java.lang.reflect.Method)) {
          throw new IllegalStateException(
              "Parameter " + p + " not declared on a method");
        }
        return new Method((java.lang.reflect.Method) exec, elements);
      }
    }

    public static class Constructor {
      private final java.lang.reflect.Constructor c;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Constructor(java.lang.reflect.Constructor c) {
        this(c, List.of());
      }

      Constructor(
        java.lang.reflect.Constructor c,
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
        this(m, List.of());
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

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Field {
      private final java.lang.reflect.Field f;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Field(java.lang.reflect.Field f) {
        this(f, List.of());
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
      private final java.lang.Class<?> c;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Class(java.lang.Class c) {
        this(c, List.of());
      }

      Class(
        java.lang.Class c,
        List<AnnotatedElement> history) {

        this.c = c;
        elements.addAll(history);
        elements.add(c);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }
  }
}
