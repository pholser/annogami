package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;
import java.util.stream.Stream;

interface EnclosurePath {
    Stream<AnnotatedElement> stream();
}
