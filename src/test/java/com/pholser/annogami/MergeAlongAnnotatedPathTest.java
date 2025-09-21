package com.pholser.annogami;

import com.pholser.annogami.annotated.Blue;
import com.pholser.annogami.annotated.X;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;
import com.pholser.annogami.annotations.Particle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.annotations.Annotations.anno;
import static com.pholser.annogami.annotations.Annotations.annoValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MergeAlongAnnotatedPathTest {
  @Test void mergeEmptyResult() throws Exception {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromField(X.class.getDeclaredField("i"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    path.merge(Particle.class, DIRECT)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void methodToDeclarerToClassAncestrySingleDirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(X.class.getDeclaredMethod("foo"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    Atom merged =
      path.merge(Atom.class, DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(2, merged.value());
    assertEquals(-1, merged.otherValue());
  }

  @Test void methodToDeclarerToClassPackageAncestrySingleMetaDirect()
    throws Exception {

    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(X.class.getDeclaredMethod("baz"))
        .toDeclaringClass()
        .toDeclaringPackage()
        .build();

    Blue blue =
      path.merge(Blue.class, META_DIRECT)
        .orElseGet(Assertions::fail);

    assertEquals(1, blue.value());
    assertEquals(-2, blue.otherValue());
    assertEquals(-93, blue.stillAnotherValue());
  }

  @Test void methodToDeclarerToClassAncestryAllDirect() throws Exception {
    AnnotatedPath path =
      AnnotatedPathBuilder.fromMethod(X.class.getDeclaredMethod("foo"))
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();

    List<Annotation> merged = path.mergeAll(DIRECT);

    assertThat(merged)
      .containsExactlyInAnyOrder(
        anno(Atom.class, Map.of("value", 2, "otherValue", -1)),
        annoValue(Iota.class, 3),
        anno(
          Blue.class,
          Map.of("value", -94, "otherValue", -2, "stillAnotherValue", -3)));
  }
}
