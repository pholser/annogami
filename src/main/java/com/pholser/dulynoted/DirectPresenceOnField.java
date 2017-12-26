package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

class DirectPresenceOnField {
    private final Field target;

    DirectPresenceOnField(Field target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
