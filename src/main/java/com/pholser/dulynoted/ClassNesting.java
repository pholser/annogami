package com.pholser.dulynoted;

import java.util.LinkedList;
import java.util.List;

class ClassNesting {
    private final Class<?> start;

    ClassNesting(Class<?> start) {
        this.start = start;
    }

    List<Class<?>> layers() {
        List<Class<?>> nesting = new LinkedList<>();

        for (Class<?> c = start; c != null; c = c.getEnclosingClass())
            nesting.add(c);

        return nesting;
    }
}
