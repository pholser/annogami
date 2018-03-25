package com.pholser.dulynoted.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({FIELD, METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
public @interface Compound {
    Particle[] value();
}
