package com.pholser.annogami.parity.junit;

import com.pholser.annogami.fixtures.A;
import com.pholser.annogami.fixtures.B;
import com.pholser.annogami.fixtures.Samples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.pholser.annogami.Presences.DIRECT;
import static com.pholser.annogami.Presences.META_PRESENT;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectAndMetaJUnitParityTest {
  private Method m;

  @BeforeEach
  void setup() throws Exception {
    m = Samples.class.getMethod("mixed", String.class);

    assumeThat(m.isAnnotationPresent(A.class)).isTrue();
    assumeThat(m.isAnnotationPresent(B.class)).isTrue();
  }

  @Test void directOnMethod() {
    String junit =
      AnnotationSupport.findAnnotation(m, A.class)
        .map(A::value)
        .orElseThrow();
    String annogami =
      DIRECT.find(A.class, m)
        .map(A::value)
        .orElseThrow();

    assertEquals(junit, annogami);
  }

  @Test void directOnParameter() {
    Parameter p = m.getParameters()[0];

    String junit =
      AnnotationSupport.findAnnotation(p, A.class)
        .map(A::value)
        .orElseThrow();
    String annogami =
      DIRECT.find(A.class, p)
        .map(A::value)
        .orElseThrow();

    assertEquals(junit, annogami);
  }

 @Test
  void metaPresentPrefersDirectWhenBothDirectAndMetaExistJUnitValue() {
   assertEquals(
     "methodA",
     AnnotationSupport.findAnnotation(m, A.class)
       .map(A::value)
       .orElseThrow());
  }

  @Test
  void metaPresentPrefersDirectWhenBothDirectAndMetaExistAnnogamiValue() {
    assertEquals(
      "methodA",
      META_PRESENT.find(A.class, m)
        .map(A::value)
        .orElseThrow());
  }

  @Test
  void metaPresentYieldsFromBWhenNoDirectAJUnitValue() throws Exception {
    Method n = Samples.OnlyB.class.getDeclaredMethod("n");

    assertEquals(
      "fromB",
      AnnotationSupport.findAnnotation(n, A.class)
        .map(A::value)
        .orElseThrow());
  }

  @Test
  void metaPresent_yieldsFromB_whenNoDirectA_DulyValue() throws Exception {
    Method n = Samples.OnlyB.class.getDeclaredMethod("n");

    assertEquals(
      "fromB",
      META_PRESENT.find(A.class, n)
        .map(A::value)
        .orElseThrow());
  }
}
