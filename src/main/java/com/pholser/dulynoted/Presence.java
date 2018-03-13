package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public final class Presence implements Detector {
    @Override
    public <A extends Annotation> Optional<A> find(
        Class<A> annotationType,
        AnnotatedElement target) {

        return Optional.ofNullable(target.getAnnotation(annotationType));
    }

    @Override
    public <A extends Annotation> List<A> findAll(
        Class<A> annotationType,
        AnnotatedElement target) {

        return Arrays.stream(target.getAnnotations())
            .filter(annotationType::isInstance)
            .map(annotationType::cast)
            .collect(toList());
    }

    @Override
    public List<Annotation> all(AnnotatedElement target) {
        List<Annotation> annotations = new ArrayList<>();
        Collections.addAll(annotations, target.getAnnotations());

        return annotations;
    }
}
