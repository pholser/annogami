package com.pholser.dulynoted;

import java.util.function.Supplier;

final class AssertionHelp {
  private AssertionHelp() {
    throw new UnsupportedOperationException();
  }

  static Supplier<AssertionError> failure(
    String template,
    Object... args) {

    return () -> new AssertionError(String.format(template, args));
  }
}
