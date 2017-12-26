package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

class DirectPresenceOnMethod {
    private final Method target;

    DirectPresenceOnMethod(Method target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
