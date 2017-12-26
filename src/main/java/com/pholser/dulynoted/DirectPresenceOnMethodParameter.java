package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;

class DirectPresenceOnMethodParameter {
    private final Parameter target;

    DirectPresenceOnMethodParameter(Parameter target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
