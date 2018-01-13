package com.pholser.dulynoted.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Repeatable(Aggregate.class)
public @interface Unit {
    int value();
}
