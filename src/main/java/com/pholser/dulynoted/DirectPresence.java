package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

class DirectPresence {
    private final AnnotatedElement target;

    DirectPresence(AnnotatedElement target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
