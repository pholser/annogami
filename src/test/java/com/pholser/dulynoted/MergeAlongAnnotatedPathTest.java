package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.Blue;
import com.pholser.dulynoted.annotated.X;
import com.pholser.dulynoted.annotations.Atom;
import org.junit.jupiter.api.Test;

import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.META_DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MergeAlongAnnotatedPathTest {
  @Test void methodToDeclarerToClassAncestryDirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromMethod(X.class.getDeclaredMethod("foo"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    Atom merged = path.merge(Atom.class, DIRECT);

    assertEquals(2, merged.value());
    assertEquals(-1, merged.otherValue());
  }

  @Test void methodToDeclarerToClassPackageAncestryMetaDirect()
    throws Exception {

    AnnotatedPath path =
      AnnotatedPath.fromMethod(X.class.getDeclaredMethod("baz"))
        .toDeclaringClass()
        .toDeclaringPackage()
        .build();

    Blue blue = path.merge(Blue.class, META_DIRECT);

    assertEquals(1, blue.value());
    assertEquals(-2, blue.otherValue());
    assertEquals(-93, blue.stillAnotherValue());
  }
}
