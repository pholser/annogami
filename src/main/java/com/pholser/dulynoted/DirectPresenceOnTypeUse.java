package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Optional;

class DirectPresenceOnTypeUse {
    private final AnnotatedType target;

    DirectPresenceOnTypeUse(AnnotatedType target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
