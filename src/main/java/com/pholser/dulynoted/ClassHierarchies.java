package com.pholser.dulynoted;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

final class ClassHierarchies {
  private ClassHierarchies() {
    throw new UnsupportedOperationException();
  }

  static List<Class<?>> depthFirstHierarchyOf(Class<?> k) {
    LinkedHashSet<Class<?>> hierarchy = new LinkedHashSet<>();
    accumulateDepthFirst(k, k, hierarchy);
    return new ArrayList<>(hierarchy);
  }

  private static void accumulateDepthFirst(
    Class<?> root,
    Class<?> k,
    LinkedHashSet<Class<?>> accumulated) {

    if (root != k) {
      accumulated.add(k);
    }

    if (k.getSuperclass() != null) {
      accumulateDepthFirst(root, k.getSuperclass(), accumulated);
    }

    Arrays.stream(k.getInterfaces())
      .forEachOrdered(i -> accumulateDepthFirst(root, i, accumulated));
  }
}
