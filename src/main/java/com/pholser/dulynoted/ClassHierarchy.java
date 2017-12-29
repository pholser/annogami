package com.pholser.dulynoted;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.*;

class ClassHierarchy {
    private final Class<?> leaf;

    ClassHierarchy(Class<?> leaf) {
        this.leaf = leaf;
    }

    List<Class<?>> depthFirst() {
        List<Class<?>> results = new ArrayList<>();
        collectDepthFirst(leaf, results);
        return results;
    }

    private static void collectDepthFirst(
        Class<?> start,
        List<Class<?>> collected) {

        if (start == Object.class)
            return;

        if (!collected.contains(start))
            collected.add(start);

        for (Class<?> c = start.getSuperclass();
            c != null;
            c = c.getSuperclass()) {

            collectDepthFirst(c, collected);
        }

        for (Class<?> iface : start.getInterfaces())
            collectDepthFirst(iface, collected);
    }

    List<Class<?>> breadthFirst() {
        if (leaf == Object.class)
            return emptyList();

        List<Class<?>> results = new LinkedList<>();

        for (Class<?> c = leaf; c != null; c = c.getSuperclass()) {
            results.add(c);
        }

        return results;
    }
}
