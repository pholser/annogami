package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class FieldEnclosurePath implements EnclosurePath {
    private final Field target;

    FieldEnclosurePath(Field target) {
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
