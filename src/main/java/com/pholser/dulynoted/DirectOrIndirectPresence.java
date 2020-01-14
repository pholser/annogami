package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.pholser.dulynoted.Annotations.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

/**
 * An object that can find annotations that are "directly present" or
 * "indirectly present" on a program element.
 */
public final class DirectOrIndirectPresence
    implements AllByTypeDetector, AllDetector {

    @Override public <A extends Annotation> List<A> findAll(
        Class<A> annotationType,
        AnnotatedElement target) {

        return asList(target.getDeclaredAnnotationsByType(annotationType));
    }

    @Override public List<Annotation> all(AnnotatedElement target) {
        List<Annotation> results = new ArrayList<>();
        Collections.addAll(results, target.getDeclaredAnnotations());

        results.addAll(
            Arrays.stream(target.getDeclaredAnnotations())
                .filter(Annotations::containsRepeatableAnnotation)
                .flatMap(a -> repeatedAnnotationsOn(a).stream())
                .collect(toList()));

        return unmodifiableList(results);
    }
}
