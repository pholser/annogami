package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

class DirectPresenceOnMethod {
    private final Method method;

    DirectPresenceOnMethod(Method method) {
        this.method = method;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            method.getDeclaredAnnotation(annotationType));
    }
}
