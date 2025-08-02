package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

class AnnotationInvocationHandler<A extends Annotation>
  implements InvocationHandler {

  private final Class<A> annoType;
  private final Map<String, Object> attrs;

  AnnotationInvocationHandler(Class<A> annoType, Map<String, Object> attrs) {
    if (!annoType.isAnnotation()
      || annoType.getInterfaces().length != 1
      || !Annotation.class.equals(annoType.getInterfaces()[0])) {

      throw new IllegalArgumentException(
        annoType + " is not a well-formed annotation type");
    }

    this.annoType = annoType;
    this.attrs = attrs;
  }

  @Override public Object invoke(Object proxy, Method m, Object[] args) {
    if (isEquals(m)) {
      return handleEquals(proxy, args[0]);
    }
    if (isHashCode(m)) {
      return handleHashCode();
    }
    if (isAnnotationType(m)) {
      return annoType;
    }
    if (isToString(m)) {
      return handleToString();
    }

    return handleAttributeAccess(m);
  }

  private Object handleAttributeAccess(Method method) {
    Object value = attrs.get(method.getName());
    return maybeClone(value);
  }

  private Object maybeClone(Object attrValue) {
    Class<?> valueType = attrValue.getClass();
    if (!valueType.isArray() || Array.getLength(attrValue) == 0) {
      return attrValue;
    }

    if (boolean[].class.equals(valueType)) {
      return ((boolean[]) attrValue).clone();
    }
    if (byte[].class.equals(valueType)) {
      return ((byte[]) attrValue).clone();
    }
    if (char[].class.equals(valueType)) {
      return ((char[]) attrValue).clone();
    }
    if (double[].class.equals(valueType)) {
      return ((double[]) attrValue).clone();
    }
    if (float[].class.equals(valueType)) {
      return ((float[]) attrValue).clone();
    }
    if (int[].class.equals(valueType)) {
      return ((int[]) attrValue).clone();
    }
    if (long[].class.equals(valueType)) {
      return ((long[]) attrValue).clone();
    }
    if (short[].class.equals(valueType)) {
      return ((short[]) attrValue).clone();
    }

    return ((Object[]) attrValue).clone();
  }

  private boolean isEquals(Method method) {
    return "equals".equals(method.getName())
      && boolean.class.equals(method.getReturnType())
      && method.getParameterCount() == 1
      && Object.class.equals(method.getParameterTypes()[0]);
  }

  private boolean handleEquals(Object proxy, Object other) {
    if (proxy == other) {
      return true;
    }
    if (!annoType.isInstance(other)) {
      return false;
    }

    Method[] annoMethods = annoType.getDeclaredMethods();
    Map<String, Method> annoMethodsByName =
      Arrays.stream(annoMethods)
        .collect(toMap(Method::getName, identity()));
    Function<String, Object> accessor =
      valueAccessor(other, annoMethodsByName);

    return Arrays.stream(annoMethods)
      .map(Method::getName)
      .allMatch(n -> equal(attrs.get(n), accessor.apply(n)));
  }

  private boolean equal(Object first, Object second) {
    Class<?> firstType = first.getClass();
    if (firstType != second.getClass()) {
      return false;
    }

    if (!firstType.isArray()) {
      return first.equals(second);
    }
    if (boolean[].class.equals(firstType)) {
      return Arrays.equals((boolean[]) first, (boolean[]) second);
    }
    if (byte[].class.equals(firstType)) {
      return Arrays.equals((byte[]) first, (byte[]) second);
    }
    if (char[].class.equals(firstType)) {
      return Arrays.equals((char[]) first, (char[]) second);
    }
    if (double[].class.equals(firstType)) {
      return Arrays.equals((double[]) first, (double[]) second);
    }
    if (float[].class.equals(firstType)) {
      return Arrays.equals((float[]) first, (float[]) second);
    }
    if (int[].class.equals(firstType)) {
      return Arrays.equals((int[]) first, (int[]) second);
    }
    if (long[].class.equals(firstType)) {
      return Arrays.equals((long[]) first, (long[]) second);
    }
    if (short[].class.equals(firstType)) {
      return Arrays.equals((short[]) first, (short[]) second);
    }

    return Arrays.equals((Object[]) first, (Object[]) second);
  }

  private boolean isHashCode(Method m) {
    return "hashCode".equals(m.getName())
      && int.class.equals(m.getReturnType())
      && m.getParameterCount() == 0;
  }

  private int handleHashCode() {
    return attrs.entrySet().stream()
      .mapToInt(e -> attrHash(e.getKey(), e.getValue()))
      .sum();
  }

  private int attrHash(String name, Object value) {
    return (127 * name.hashCode()) ^ valueHash(value);
  }

  private int valueHash(Object value) {
    Class<?> valueType = value.getClass();
    if (!valueType.isArray()) {
      return value.hashCode();
    }

    if (boolean[].class.equals(valueType)) {
      return Arrays.hashCode((boolean[]) value);
    }
    if (byte[].class.equals(valueType)) {
      return Arrays.hashCode((byte[]) value);
    }
    if (char[].class.equals(valueType)) {
      return Arrays.hashCode((char[]) value);
    }
    if (double[].class.equals(valueType)) {
      return Arrays.hashCode((double[]) value);
    }
    if (float[].class.equals(valueType)) {
      return Arrays.hashCode((float[]) value);
    }
    if (int[].class.equals(valueType)) {
      return Arrays.hashCode((int[]) value);
    }
    if (long[].class.equals(valueType)) {
      return Arrays.hashCode((long[]) value);
    }
    if (short[].class.equals(valueType)) {
      return Arrays.hashCode((short[]) value);
    }

    return Arrays.hashCode((Object[]) value);
  }

  private boolean isAnnotationType(Method m) {
    return "annotationType".equals(m.getName())
      && Class.class.equals(m.getReturnType())
      && m.getParameterCount() == 0;
  }

  private boolean isToString(Method m) {
    return "toString".equals(m.getName())
      && String.class.equals(m.getReturnType())
      && m.getParameterCount() == 0;
  }

  private String handleToString() {
    return "merged "
      + annoType.getName()
      + '['
      + attrs.entrySet().stream()
          .map(e -> attrString(e.getKey(), e.getValue()))
          .collect(joining(", "))
      + ']';
  }

  private String attrString(String name, Object value) {
    return name + '=' + valueString(value);
  }

  private String valueString(Object value) {
    Class<?> valueType = value.getClass();
    if (!valueType.isArray()) {
      return value.toString();
    }

    if (boolean[].class.equals(valueType)) {
      return Arrays.toString((boolean[]) value);
    }
    if (byte[].class.equals(valueType)) {
      return Arrays.toString((byte[]) value);
    }
    if (char[].class.equals(valueType)) {
      return Arrays.toString((char[]) value);
    }
    if (double[].class.equals(valueType)) {
      return Arrays.toString((double[]) value);
    }
    if (float[].class.equals(valueType)) {
      return Arrays.toString((float[]) value);
    }
    if (int[].class.equals(valueType)) {
      return Arrays.toString((int[]) value);
    }
    if (long[].class.equals(valueType)) {
      return Arrays.toString((long[]) value);
    }
    if (short[].class.equals(valueType)) {
      return Arrays.toString((short[]) value);
    }

    return Arrays.toString((Object[]) value);
  }

  private Function<String, Object> valueAccessor(
    Object other,
    Map<String, Method> annoMethodsByName) {

    InvocationHandler invocation = Proxy.getInvocationHandler(other);
    if (Proxy.isProxyClass(other.getClass())
      && invocation instanceof AnnotationInvocationHandler<?> handler) {

      return handler.attrs::get;
    }

    return name -> Reflection.invoke(annoMethodsByName.get(name), other);
  }
}
