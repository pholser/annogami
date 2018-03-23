package com.pholser.dulynoted;

import java.util.function.Supplier;

public final class AssertionHelp {
    private AssertionHelp() {
        throw new UnsupportedOperationException();
    }

    public static Supplier<AssertionError> failure(
        String template,
        Object... args) {

        return () -> new AssertionError(String.format(template, args));
    }
}
