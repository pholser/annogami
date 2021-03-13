package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.X;
import com.pholser.dulynoted.annotations.Atom;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.Presences.DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MergeDirectAlongAnnotatedPathTest {
  @Test void methodToDeclarerToClassAncestry() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromMethod(X.class.getDeclaredMethod("foo"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    Atom merged = path.merge(Atom.class, DIRECT);

    assertEquals(2, merged.value());
    assertEquals(-1, merged.otherValue());
  }
}
