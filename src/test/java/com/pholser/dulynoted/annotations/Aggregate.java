package com.pholser.dulynoted.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
public @interface Aggregate {
    Unit[] value();
}
