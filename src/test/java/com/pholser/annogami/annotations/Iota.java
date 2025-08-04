package com.pholser.annogami.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Inherited
public @interface Iota {
  int value();
}
