package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class MetaDirectPresence
    implements SingleByTypeDetector,
        AllByTypeDetector,
        AllDetector {

    private final DirectPresence direct = new DirectPresence();

    @Override public <A extends Annotation> Optional<A> find(
        Class<A> annotationType,
        AnnotatedElement target) {

        // TODO: need to specify ordering
        return direct.find(annotationType, target)
            .or(() ->
                direct.all(target).stream()
                    .flatMap(a ->
                        find(annotationType, a.annotationType()).stream())
                .findFirst());
    }

    @Override
    public <A extends Annotation> List<A> findAll(
        Class<A> annotationType,
        AnnotatedElement target) {

        return Collections.emptyList();
    }

    @Override public List<Annotation> all(AnnotatedElement target) {
        return Collections.emptyList();
    }
}
