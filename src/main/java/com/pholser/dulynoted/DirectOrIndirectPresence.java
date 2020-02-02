package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * An object that can find annotations that are "directly present" or
 * "indirectly present" on a program element.
 */
public final class DirectOrIndirectPresence implements AllByTypeDetector {
    @Override public <A extends Annotation> List<A> findAll(
        Class<A> annotationType,
        AnnotatedElement target) {

        return asList(target.getDeclaredAnnotationsByType(annotationType));
    }
}
