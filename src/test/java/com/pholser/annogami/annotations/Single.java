package com.pholser.annogami.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Repeatable(Many.class)
@Inherited
public @interface Single {
  int value();
}
