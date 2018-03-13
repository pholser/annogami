package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;

interface Detector {
    <A extends Annotation>
    Optional<A> find(Class<A> annotationType, AnnotatedElement target);

    <A extends Annotation>
    List<A> findAll(Class<A> annotationType, AnnotatedElement target);

    List<Annotation> all(AnnotatedElement target);
}
