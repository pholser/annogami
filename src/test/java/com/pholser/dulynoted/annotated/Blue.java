package com.pholser.dulynoted.annotated;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Red(2) @Green(3)
public @interface Blue {
  int value();

  int otherValue() default -2;

  int stillAnotherValue() default -3;
}
