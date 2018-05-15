package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

interface AllDetector {
    List<Annotation> all(AnnotatedElement target);
}
