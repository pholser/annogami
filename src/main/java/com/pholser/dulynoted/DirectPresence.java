package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

class DirectPresence implements Presence {
    @Override
    public <A extends Annotation>
    Optional<A> find(Class<A> annotationType, AnnotatedElement target) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
