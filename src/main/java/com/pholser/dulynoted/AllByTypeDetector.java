package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

interface AllByTypeDetector {
    <A extends Annotation>
    List<A> findAll(Class<A> annotationType, AnnotatedElement target);
}
