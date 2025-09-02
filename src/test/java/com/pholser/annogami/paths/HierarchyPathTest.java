package com.pholser.annogami.paths;

import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.AnnotationAssertions;
import com.pholser.annogami.fixtures.A;
import com.pholser.annogami.fixtures.Samples.Parents.Base;
import com.pholser.annogami.fixtures.Samples.Parents.Derived;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HierarchyPathTest {
  @Test void childMethodDirectLookupEmptyByDefault() throws Exception {
    Method derived = Derived.class.getMethod("m", String.class);

    DIRECT.find(A.class, derived)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void mergePrefersParameterOverMethodClassHierarchy() throws Exception {
    Method derived = Derived.class.getMethod("m", String.class);

    A merged =
      AnnotatedPath.fromParameter(derived.getParameters()[0])
        .toDeclaringMethod()
        .toDeclaringClass()
        .toDepthHierarchy()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("paramA", merged.value());
  }

  @Test void mergeFallsBackToBaseMethodWhenParamAbsent() throws Exception {
    Method base = Base.class.getMethod("m", String.class);

    A merged =
      AnnotatedPath.fromParameter(base.getParameters()[0]) // no @A here
        .toDeclaringMethod()
        .toDeclaringClass()
        .toDepthHierarchy()
        .build()
        .merge(A.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals("baseMethod", merged.value());
  }
}
