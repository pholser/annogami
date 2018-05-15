package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

interface SingleByTypeDetector {
    <A extends Annotation>
    Optional<A> find(Class<A> annotationType, AnnotatedElement target);
}
