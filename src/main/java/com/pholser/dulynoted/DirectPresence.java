package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class DirectPresence
    implements SingleByTypeDetector, AllDetector {

    @Override
    public <A extends Annotation> Optional<A> find(
        Class<A> annotationType,
        AnnotatedElement target) {

        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }

    @Override
    public List<Annotation> all(AnnotatedElement target) {
        List<Annotation> results = new ArrayList<>();
        Collections.addAll(results, target.getDeclaredAnnotations());

        return results;
    }
}
