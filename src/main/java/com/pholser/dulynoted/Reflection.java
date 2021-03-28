package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

final class Reflection {
  private Reflection() {
    throw new UnsupportedOperationException();
  }

  static Optional<Method> findMethod(
    Class<?> k,
    String name,
    Class<?>[] parameterTypes) {

    try {
      return Optional.of(k.getDeclaredMethod(name, parameterTypes));
    } catch (NoSuchMethodException ex) {
      return Optional.empty();
    }
  }

  static <A extends Annotation> Map<String, Object> attributes(
    Class<A> annoType,
    A instance) {

    return Arrays.stream(annoType.getDeclaredMethods())
      .collect(toMap(Method::getName, m -> invoke(m, instance)));
  }

  static <A extends Annotation> Map<String, Object> defaultValues(
    Class<A> annoType) {

    return Arrays.stream(annoType.getDeclaredMethods())
      .map(m -> new SimpleEntry<>(m.getName(), m.getDefaultValue()))
      .filter(e -> e.getValue() != null)
      .collect(toMap(Entry::getKey, Entry::getValue));
  }

  static Object invoke(Method m, Object receiver, Object... args) {
    try {
      return m.invoke(receiver, args);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
