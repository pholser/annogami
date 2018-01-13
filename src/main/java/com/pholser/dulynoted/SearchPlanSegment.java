package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

class SearchPlanSegment {
    private final AnnotatedElement target;
    private final Presence presence;

    SearchPlanSegment(AnnotatedElement target, Presence presence) {
        this.target = target;
        this.presence = presence;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return presence.find(annotationType, target);
    }
}
