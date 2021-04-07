package com.pholser.dulynoted;

import com.pholser.dulynoted.annotated.Blue;
import com.pholser.dulynoted.annotated.X;
import com.pholser.dulynoted.annotations.Annotations;
import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import static com.pholser.dulynoted.Presences.DIRECT;
import static com.pholser.dulynoted.Presences.META_DIRECT;
import static com.pholser.dulynoted.annotations.Annotations.anno;
import static com.pholser.dulynoted.annotations.Annotations.annoValue;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MergeAlongAnnotatedPathTest {
  @Test void methodToDeclarerToClassAncestrySingleDirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromMethod(X.class.getDeclaredMethod("foo"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    Atom merged = path.merge(Atom.class, DIRECT);

    assertEquals(2, merged.value());
    assertEquals(-1, merged.otherValue());
  }

  @Test void methodToDeclarerToClassPackageAncestrySingleMetaDirect()
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

  @Test void methodToDeclarerToClassAncestryAllDirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPath.fromMethod(X.class.getDeclaredMethod("foo"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    List<Annotation> merged = path.mergeAll(DIRECT);

    assertThat(
      merged,
      containsInAnyOrder(
        anno(Atom.class, Map.of("value", 2, "otherValue", -1)),
        annoValue(Iota.class, 3),
        anno(
          Blue.class,
          Map.of("value", -94, "otherValue", -2, "stillAnotherValue", -3))));
  }
}
