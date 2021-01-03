package com.pholser.dulynoted;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

final class ClassHierarchies {
  private ClassHierarchies() {
    throw new UnsupportedOperationException();
  }

  static List<Class<?>> depthFirstHierarchyOf(Class<?> k) {
    Deque<Class<?>> visitees = new ArrayDeque<>();
    visitees.push(k);

    LinkedHashSet<Class<?>> hierarchy = new LinkedHashSet<>();
    while (!visitees.isEmpty()) {
      Class<?> next = visitees.pop();
      if (next != k)
        hierarchy.add(next);

      for (int i = next.getInterfaces().length - 1; i >= 0; --i)
        visitees.push(next.getInterfaces()[i]);

      if (next.getSuperclass() != null)
        visitees.push(next.getSuperclass());
    }

    return new ArrayList<>(hierarchy);
  }

  static List<Class<?>> breadthFirstHierarchyOf(Class<?> k) {
    Queue<Class<?>> visitees = new LinkedList<>();
    visitees.add(k);

    LinkedHashSet<Class<?>> hierarchy = new LinkedHashSet<>();
    while (!visitees.isEmpty()) {
      Class<?> next = visitees.remove();
      if (next != k)
        hierarchy.add(next);

      if (next.getSuperclass() != null)
        visitees.add(next.getSuperclass());

      visitees.addAll(Arrays.asList(next.getInterfaces()));
    }

    return new ArrayList<>(hierarchy);
  }
}
