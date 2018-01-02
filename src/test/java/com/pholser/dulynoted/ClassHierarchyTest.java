package com.pholser.dulynoted;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

class ClassHierarchyTest {
    @Test void javaLangObjectDepthFirstUnique() {
        assertEquals(
            emptyList(),
            new ClassHierarchy(Object.class).depthFirst());
    }

    @Test void javaLangObjectBreadthFirstUnique() {
        assertEquals(
            emptyList(),
            new ClassHierarchy(Object.class).breadthFirst());
    }

    @Test void leafDepthFirstUnique() {
        assertEquals(
            asList(
                Child.class,
                Parent.class,
                Grandparent.class,
                Bar.class,
                Foo.class,
                Serializable.class,
                Comparable.class,
                Cloneable.class),
            new ClassHierarchy(Child.class)
                .depthFirst()
                .stream()
                .distinct()
                .collect(toList()));
    }

    @Test void leafBreadthFirstUnique() {
        assertEquals(
            asList(
                Child.class,
                Parent.class,
                Serializable.class,
                Grandparent.class,
                Comparable.class,
                Cloneable.class,
                Bar.class,
                Foo.class),
            new ClassHierarchy(Child.class)
                .breadthFirst()
                .stream()
                .distinct()
                .collect(toList()));
    }

    @Test void leafDepthFirstNonUnique() {
        assertEquals(
            asList(
                Child.class,
                Parent.class,
                Grandparent.class,
                Bar.class,
                Foo.class,
                Serializable.class,
                Comparable.class,
                Cloneable.class,
                Serializable.class),
            new ClassHierarchy(Child.class).depthFirst());
    }

    @Test void leafBreadthFirstNonUnique() {
        assertEquals(
            asList(
                Child.class,
                Parent.class,
                Serializable.class,
                Grandparent.class,
                Comparable.class,
                Cloneable.class,
                Bar.class,
                Foo.class,
                Serializable.class),
            new ClassHierarchy(Child.class).breadthFirst());
    }

    private static class Grandparent implements Bar {
    }

    private static class Parent
        extends Grandparent
        implements Comparable<Parent>, Cloneable {

        @Override public int compareTo(Parent o) {
            return 0;
        }
    }

    private static final class Child
        extends Parent
        implements Serializable {

        private static final long serialVersionUID = Integer.MIN_VALUE;
    }

    private interface Foo {
    }

    private interface Bar extends Foo, Serializable {
    }
}
