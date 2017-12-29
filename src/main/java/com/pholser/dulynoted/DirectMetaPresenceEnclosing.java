package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

class DirectMetaPresenceEnclosing {
    private final Method target;

    DirectMetaPresenceEnclosing(Method target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return new DirectMetaPresence(target).find(annotationType);
    }
}
