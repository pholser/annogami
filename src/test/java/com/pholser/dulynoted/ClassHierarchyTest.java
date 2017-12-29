package com.pholser.dulynoted;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

class ClassHierarchyTest {
    @Test void javaLangObjectDepthFirst() {
        assertEquals(
            emptyList(),
            new ClassHierarchy(Object.class).depthFirst());
    }

    @Test void javaLangObjectBreadthFirst() {
        assertEquals(
            emptyList(),
            new ClassHierarchy(Object.class).breadthFirst());
    }

    @Test void leafDepthFirst() {
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
            new ClassHierarchy(Child.class).depthFirst());
    }

    @Test void leafBreadthFirst() {
        assertEquals(
            asList(
                Child.class,
                Parent.class,
                Serializable.class,
                Grandparent.class,
                Comparable.class,
                Cloneable.class,
                Object.class,
                Bar.class,
                Foo.class),
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
