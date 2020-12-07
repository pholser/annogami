package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnnotatedPath {
  private final List<AnnotatedElement> elements;

  private AnnotatedPath(List<AnnotatedElement> elements) {
    this.elements = elements;
  }

  public static Builder.Parameter fromParameter(Parameter parm) {
    return new Builder.Parameter(parm);
  }

  public static Builder.Method fromMethod(Method m) {
    return new Builder.Method(m);
  }

  public static Builder.Constructor fromConstructor(Constructor<?> ctor) {
    return new Builder.Constructor(ctor);
  }

  public static Builder.Field fromField(Field f) {
    return new Builder.Field(f);
  }

  public static Builder.Class fromClass(Class<?> clazz) {
    return new Builder.Class(clazz);
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
      private final java.lang.reflect.Parameter parm;

      Parameter(java.lang.reflect.Parameter parm) {
        this.parm = parm;
        elements.add(parm);
      }

      public Constructor toDeclaringConstructor() {
        Executable exec = parm.getDeclaringExecutable();
        if (!(exec instanceof java.lang.reflect.Constructor<?>)) {
          throw new IllegalStateException(
            "Parameter " + parm + " not declared on a constructor");
        }
        return new Constructor((java.lang.reflect.Constructor) exec, elements);
      }

      public Method toDeclaringMethod() {
        Executable exec = parm.getDeclaringExecutable();
        if (!(exec instanceof java.lang.reflect.Method)) {
          throw new IllegalStateException(
              "Parameter " + parm + " not declared on a method");
        }
        return new Method((java.lang.reflect.Method) exec, elements);
      }
    }

    public static class Constructor {
      private final java.lang.reflect.Constructor ctor;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Constructor(java.lang.reflect.Constructor ctor) {
        this(ctor, List.of());
      }

      Constructor(
        java.lang.reflect.Constructor ctor,
        List<AnnotatedElement> history) {

        this.ctor = ctor;
        elements.addAll(history);
        elements.add(ctor);
      }

      public Class toDeclaringClass() {
        return new Class(ctor.getDeclaringClass(), elements);
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
      private final java.lang.Class<?> clazz;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Class(java.lang.Class clazz) {
        this(clazz, List.of());
      }

      Class(
        java.lang.Class clazz,
        List<AnnotatedElement> history) {

        this.clazz = clazz;
        elements.addAll(history);
        elements.add(clazz);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }

      public Package toDeclaringPackage() {
        return new Package(clazz.getPackage(), elements);
      }

      public Module toDeclaringModule() {
        return new Module(clazz.getModule(), elements);
      }
    }

    public static class Package{
      private final java.lang.Package pkg;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Package(java.lang.Package pkg) {
        this(pkg, List.of());
      }

      Package(
        java.lang.Package pkg,
        List<AnnotatedElement> history) {

        this.pkg = pkg;
        elements.addAll(history);
        elements.add(pkg);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }

    public static class Module {
      private final java.lang.Module mod;
      private final List<AnnotatedElement> elements = new ArrayList<>();

      Module(java.lang.Module mod) {
        this(mod, List.of());
      }

      Module(
        java.lang.Module mod,
        List<AnnotatedElement> history) {

        this.mod = mod;
        elements.addAll(history);
        elements.add(mod);
      }

      public AnnotatedPath build() {
        return new AnnotatedPath(elements);
      }
    }
  }
}
