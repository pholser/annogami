package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.util.Optional;

class DirectPresenceOnType {
    private final Class<?> target;

    DirectPresenceOnType(Class<?> target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
