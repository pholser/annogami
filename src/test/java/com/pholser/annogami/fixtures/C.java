package com.pholser.annogami.fixtures;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@A
public @interface C {
  @AliasFor(annotation = A.class, attribute = "value")
  String aliasToA() default "aliased";
}
