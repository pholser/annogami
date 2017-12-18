package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Optional;

class DirectPresenceOnConstructor {
    private final Constructor<?> ctor;

    DirectPresenceOnConstructor(Constructor<?> ctor) {
        this.ctor = ctor;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            ctor.getDeclaredAnnotation(annotationType));
    }
}
