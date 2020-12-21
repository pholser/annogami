package com.pholser.dulynoted.annotated;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Red(4) @Blue(5)
public @interface Green {
  int value();
}
