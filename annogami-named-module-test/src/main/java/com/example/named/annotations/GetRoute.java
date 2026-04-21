package com.example.named.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Route
public @interface GetRoute {
  @AliasFor(annotation = Route.class, attribute = "path")
  String value() default "";
}
