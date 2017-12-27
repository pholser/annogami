package com.pholser.dulynoted.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE_PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface C {
    int value();
}
