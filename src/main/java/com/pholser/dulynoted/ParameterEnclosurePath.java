package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ParameterEnclosurePath implements EnclosurePath {
    private final Parameter target;

    ParameterEnclosurePath(Parameter target) {
        this.target = target;
    }

    @Override public Stream<AnnotatedElement> stream() {
        List<AnnotatedElement> enclosures = new ArrayList<>();
        enclosures.add(target);
        enclosures.add(target.getDeclaringExecutable());

        Class<?> declaringClass = target.getDeclaringExecutable().getDeclaringClass();
        enclosures.addAll(new ClassNesting(declaringClass).layers());
        if (declaringClass.getPackage() != null)
            enclosures.add(declaringClass.getPackage());

        return enclosures.stream();
    }
}
