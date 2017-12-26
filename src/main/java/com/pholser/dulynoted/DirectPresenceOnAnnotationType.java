package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.util.Optional;

class DirectPresenceOnAnnotationType {
    private final Class<? extends Annotation> target;

    DirectPresenceOnAnnotationType(Class<? extends Annotation> target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> metaType) {
        return Optional.ofNullable(target.getDeclaredAnnotation(metaType));
    }
}
