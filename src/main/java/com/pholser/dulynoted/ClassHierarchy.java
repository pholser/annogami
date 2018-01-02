package com.pholser.dulynoted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class ClassHierarchy {
    private final Class<?> leaf;

    ClassHierarchy(Class<?> leaf) {
        this.leaf = leaf;
    }

    List<Class<?>> depthFirstUnique() {
        List<Class<?>> collected = new LinkedList<>();

        Deque<Class<?>> unconsidered = new LinkedList<>();
        unconsidered.push(leaf);

        while (!unconsidered.isEmpty()) {
            Class<?> candidate = unconsidered.pop();
            if (candidate != Object.class) {
                if (!collected.contains(candidate))
                    collected.add(candidate);

                interfacesOf(candidate).forEach(unconsidered::push);
                superclassHierarchyOf(candidate).forEach(unconsidered::push);
            }
        }

        return collected;
    }

    List<Class<?>> breadthFirstUnique() {
        List<Class<?>> collected = new LinkedList<>();

        Queue<Class<?>> unconsidered = new LinkedList<>();
        unconsidered.offer(leaf);

        while (!unconsidered.isEmpty()) {
            Class<?> candidate = unconsidered.remove();

            if (candidate != Object.class) {
                if (!collected.contains(candidate))
                collected.add(candidate);

                if (candidate.getSuperclass() != null)
                    unconsidered.offer(candidate.getSuperclass());

                Arrays.stream(candidate.getInterfaces())
                    .forEach(unconsidered::offer);
            }
        }

        return collected;
    }

    private static List<Class<?>> superclassHierarchyOf(Class<?> target) {
        List<Class<?>> hierarchy = new ArrayList<>();

        for (Class<?> c = target.getSuperclass();
            c != null;
            c = c.getSuperclass()) {

            hierarchy.add(c);
        }

        Collections.reverse(hierarchy);

        return hierarchy;
    }

    private static List<Class<?>> interfacesOf(Class<?> target) {
        List<Class<?>> interfaces = new ArrayList<>();
        Collections.addAll(interfaces, target.getInterfaces());
        Collections.reverse(interfaces);

        return interfaces;
    }
}
