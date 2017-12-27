package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;
import java.util.Optional;

class DirectPresenceOnTypeVariable<D extends GenericDeclaration> {
    private final TypeVariable<D> target;

    DirectPresenceOnTypeVariable(TypeVariable<D> target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
