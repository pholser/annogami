package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class MethodEnclosurePath implements EnclosurePath {
    private final Method target;

    MethodEnclosurePath(Method target) {
        this.target = target;
    }

    @Override public List<AnnotatedElement> enclosures() {
        List<AnnotatedElement> enclosures = new ArrayList<>();
        enclosures.add(target);
        enclosures.addAll(new ClassNesting(target.getDeclaringClass()).layers());
        if (target.getDeclaringClass().getPackage() != null)
            enclosures.add(target.getDeclaringClass().getPackage());

        return enclosures;
    }
}
