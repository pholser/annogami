package com.pholser.annogami.parity.spring;

import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.fixtures.A;
import com.pholser.annogami.fixtures.Samples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.reflect.Method;

import static com.pholser.annogami.Presences.META_PRESENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MergeSpringParityTest {
  private Method m;

  @BeforeEach void setUp() throws Exception {
    m = Samples.class.getMethod("mixed", String.class);
  }

  @Test void metaPresentValueVsSpringMergedValue() {
    String spring =
      MergedAnnotations.from(m)
        .get(A.class)
        .getString("value");
    String annogami =
      AnnotatedPath.fromMethod(m)
        .build()
        .merge(A.class, META_PRESENT)
        .map(A::value)
        .orElseGet(Assertions::fail);

    assertEquals(spring, annogami);
  }
}
