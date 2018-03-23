package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public final class DirectOrIndirectPresence implements Detector {
    @Override
    public <A extends Annotation> Optional<A> find(
        Class<A> annotationType,
        AnnotatedElement target) {

        A[] annotations = target.getDeclaredAnnotationsByType(annotationType);
        return annotations.length == 1
            ? Optional.of(annotations[0])
            : Optional.empty();
    }

    @Override
    public <A extends Annotation> List<A> findAll(
        Class<A> annotationType,
        AnnotatedElement target) {

        List<A> results = new ArrayList<>();
        Collections.addAll(
            results,
            target.getDeclaredAnnotationsByType(annotationType));

        return results;
    }

    @Override
    public List<Annotation> all(AnnotatedElement target) {
        List<Annotation> directOrIndirect = new ArrayList<>();
        Collections.addAll(directOrIndirect, target.getDeclaredAnnotations());

        directOrIndirect.addAll(
            Arrays.stream(target.getDeclaredAnnotations())
                .filter(Annotations::containsRepeatableAnnotation)
                .flatMap(a -> Annotations.repeatedAnnotationsOn(a).stream())
                .collect(toList()));

        return directOrIndirect;
    }
}
