package com.pholser.annogami.parity.junit;

import com.pholser.annogami.fixtures.Samples;
import com.pholser.annogami.fixtures.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RepeatablesJUnitParityTest {
  private Method m;

  @BeforeEach void setup() throws Exception {
    m = Samples.class.getMethod("mixed", String.class);
  }

  @Test void repeatablesCount() {
    assertEquals(3, DIRECT_OR_INDIRECT.findAll(Tag.class, m).size());
  }

  @Test
  void repeatablesCountEqualsJUnit() {
    assertEquals(
      junitValues().size(),
      DIRECT_OR_INDIRECT.findAll(Tag.class, m).size());
  }

  @Test void repeatablesValuesContents() {
    assertThat(annogamiValues()).containsExactlyInAnyOrder("x", "y", "z");
  }

  // ----- Ordering (don’t hardcode; assert parity with JUnit) -----
  @Test
  void repeatablesOrderEqualsJUnitOrder() {
    assertEquals(junitValues(), annogamiValues());
  }

  private List<String> junitValues() {
    return AnnotationSupport.findRepeatableAnnotations(m, Tag.class)
      .stream().map(Tag::value).toList();
  }

  private List<String> annogamiValues() {
    return DIRECT_OR_INDIRECT.findAll(Tag.class, m)
      .stream().map(Tag::value).toList();
  }
}
