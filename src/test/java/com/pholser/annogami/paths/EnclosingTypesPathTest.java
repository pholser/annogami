package com.pholser.annogami.paths;

import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.fixtures.A;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.GrandOuter;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.GrandOuter.Middle2;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.GrandOuter.Middle2.Leaf2;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.Outer;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.Outer.InnerNoOwnA;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.Outer2;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.Outer2.Middle;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.Outer2.Middle.Leaf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.pholser.annogami.Presences.META_DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnclosingTypesPathTest {
  @Test
  void methodMergePicksImmediateEnclosing() throws Exception {
    InnerNoOwnA inner = new Outer().new InnerNoOwnA();
    Method m = inner.getClass().getMethod("m");

    A merged =
      AnnotatedPath.fromMethod(m)
        .toDeclaringClass()
        .toClassEnclosure()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("outer", merged.value());
  }

  @Test void nearestEnclosingBeatsFurther() throws Exception {
    Middle mid = new Outer2().new Middle();
    Leaf leaf = mid.new Leaf();
    Method m = leaf.getClass().getMethod("m");

    A merged =
      AnnotatedPath.fromMethod(m)
        .toDeclaringClass()
        .toClassEnclosure()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("middle", merged.value());
  }

  @Test
  void outermostUsedWhenNoNearerAnnotations() throws Exception {
    Middle2 mid = new GrandOuter().new Middle2();
    Leaf2 leaf = mid.new Leaf2();
    Method m = leaf.getClass().getMethod("m");

    A merged =
      AnnotatedPath.fromMethod(m)
        .toDeclaringClass()
        .toClassEnclosure()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("grandOuter", merged.value());
  }
}
