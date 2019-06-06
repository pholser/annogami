package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;

public final class DirectPresence
    implements SingleByTypeDetector, AllDetector {

    @Override public <A extends Annotation> Optional<A> find(
        Class<A> annotationType,
        AnnotatedElement target) {

        return Optional.ofNullable(
            target.getDeclaredAnnotation(annotationType));
    }

    @Override public List<Annotation> all(AnnotatedElement target) {
        return unmodifiableList(
            Arrays.asList(target.getDeclaredAnnotations()));
    }
}
