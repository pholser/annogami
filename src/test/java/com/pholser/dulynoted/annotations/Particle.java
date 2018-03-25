package com.pholser.dulynoted.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({FIELD, METHOD, TYPE})
@Retention(RUNTIME)
@Repeatable(Compound.class)
@Inherited
public @interface Particle {
    int value();
}
