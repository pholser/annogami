package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.util.Optional;

class DirectPresenceOnPackage {
    private final Package target;

    DirectPresenceOnPackage(Package target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }
}
