package com.pholser.annogami.paths;

import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.fixtures.A;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.ImplementsAandB;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.ImplementsI1ThenI2;
import com.pholser.annogami.fixtures.InterfacesAndEnclosures.OverridesMethodButUnannotated;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.pholser.annogami.Presences.META_DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InterfacesPathTest {
  @Test void typeLevelInterfaceAnnotationReachedViaBreadthHierarchy()
    throws Exception {

    Method m = ImplementsAandB.class.getMethod("m");

    A merged =
      AnnotatedPath.fromMethod(m)
        .toDeclaringClass()
        .toBreadthHierarchy()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("ifaceTypeA", merged.value());
  }

  @Test void methodLevelInterfaceAnnotationReachedViaBreadthHierarchy()
    throws Exception {

    Method m = OverridesMethodButUnannotated.class.getMethod("m");

    A merged =
      AnnotatedPath.fromMethod(m)
        .toBreadthOverridden()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("ifaceMethodA", merged.value());
  }

  @Test void multipleInterfacesBreadthFirstPrefersFirstDeclared() {
    A merged =
      AnnotatedPath.fromClass(ImplementsI1ThenI2.class)
        .toBreadthHierarchy()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("i1", merged.value());
  }
}
