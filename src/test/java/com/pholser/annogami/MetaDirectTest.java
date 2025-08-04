package com.pholser.annogami;

import com.pholser.annogami.annotated.Blue;
import com.pholser.annogami.annotated.Green;
import com.pholser.annogami.annotated.Red;
import com.pholser.annogami.annotated.X;
import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Compound;
import com.pholser.annogami.annotations.Particle;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.annotations.Annotations.anno;
import static com.pholser.annogami.annotations.Annotations.annoValue;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MetaDirectTest {
  @Test void atomOnField() throws Exception {
    Atom a =
      META_DIRECT.find(Atom.class, X.class.getDeclaredField("i"))
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(1, a.value());
  }

  @Test void retentionOnFieldMeta() throws Exception {
    Retention r =
      META_DIRECT.find(Retention.class, X.class.getDeclaredField("i"))
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(RUNTIME, r.value());
  }

  @Test void inheritedOnFieldMeta() throws Exception {
    META_DIRECT.find(Inherited.class, X.class.getDeclaredField("i"))
      .orElseGet(() -> fail("Missing annotation"));
  }

  @Test void documentedOnFieldMeta() throws Exception {
    META_DIRECT.find(Documented.class, X.class.getDeclaredField("i"))
      .orElseGet(() -> fail("Missing annotation"));
  }

  @Test void overrideOnFieldMeta() throws Exception {
    META_DIRECT.find(Override.class, X.class.getDeclaredField("i"))
      .ifPresent(d -> fail("Should not have found annotation " + d));
  }

  @Test void particleOnField() throws Exception {
    Particle p =
      META_DIRECT.find(Particle.class, X.class.getDeclaredField("s"))
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(4, p.value());
  }

  @Test void repeatableOnField() throws Exception {
    Repeatable r =
      META_DIRECT.find(Repeatable.class, X.class.getDeclaredField("s"))
        .orElseGet(() -> fail("Missing annotation"));

    assertEquals(Compound.class, r.value());
  }

  @Test void repeatedAnnotation() throws Exception {
    META_DIRECT.find(Particle.class, X.class.getDeclaredMethod("bar"))
      .ifPresent(p ->
        fail("Particle " + p + " should not be directly present here"));
  }

  @Test void allMetaOnMethod() throws Exception {
    List<Annotation> expected =
      List.of(
        annoValue(Red.class, 10),
        annoValue(Retention.class, RUNTIME),
        anno(Documented.class),
        anno(Documented.class),
        annoValue(Retention.class, RUNTIME),
        annoValue(Target.class, new ElementType[] { ANNOTATION_TYPE }),
        anno(Documented.class),
        annoValue(Retention.class, RUNTIME),
        annoValue(Target.class, new ElementType[] { ANNOTATION_TYPE }),
        annoValue(Retention.class, RUNTIME),
        annoValue(Target.class, new ElementType[] { ANNOTATION_TYPE }),
        anno(Blue.class, Map.of("value", 1, "stillAnotherValue", -93)),
        annoValue(Retention.class, RUNTIME),
        annoValue(Red.class, 2),
        annoValue(Green.class, 3),
        annoValue(Retention.class, RUNTIME),
        annoValue(Red.class, 4),
        annoValue(Blue.class, 5),
        annoValue(Green.class, 6));

    assertEquals(
      expected,
      META_DIRECT.all(X.class.getDeclaredMethod("baz")));
  }
}
