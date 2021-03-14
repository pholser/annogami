package com.pholser.dulynoted.annotated;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Blue(value = 1, stillAnotherValue = -93) @Green(6)
public @interface Red {
  int value();
}
