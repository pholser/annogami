package com.pholser.annogami.paths;

import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.AnnotationAssertions;
import com.pholser.annogami.fixtures.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassInheritanceTest {
  @A("parent") static class Parent {}
  static class Child extends Parent { void k() {} }

  @Test void directOnChildAbsent() {
    DIRECT.find(A.class, Child.class)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void pathMergeCanPickParentFromChildMethod() throws Exception {
    A merged =
      AnnotatedPath.fromMethod(Child.class.getDeclaredMethod("k"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("parent", merged.value());
  }
}
