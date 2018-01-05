package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ClassEnclosurePath implements EnclosurePath {
    private final Class<?> target;

    ClassEnclosurePath(Class<?> target) {
        this.target = target;
    }

    @Override public Stream<AnnotatedElement> stream() {
        List<AnnotatedElement> enclosures = new ArrayList<>();
        enclosures.add(target);
        enclosures.addAll(new ClassNesting(target.getDeclaringClass()).layers());
        if (target.getDeclaringClass().getPackage() != null)
            enclosures.add(target.getDeclaringClass().getPackage());

        return enclosures.stream();
    }
}
