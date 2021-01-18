package com.pholser.dulynoted;

import java.lang.reflect.Method;
import java.util.Optional;

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
}
