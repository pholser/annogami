package com.pholser.annogami;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectPresenceTest {
  @Retention(RUNTIME)
  @interface A {
    int value();
  }

  @Retention(RUNTIME)
  @interface C {
    char value();
  }

  @Retention(RUNTIME)
  @interface Cs {
    Bs[] value();

    C[] cs() default {};

    String name() default "";
  }

  @Repeatable(Cs.class)
  @Retention(RUNTIME)
  @interface Bs {
    B[] value();

    int position() default 0;
  }

  @Repeatable(Bs.class)
  @Retention(RUNTIME)
  @interface B {
    String value();
  }

  @A(3)
  static class AHaver {
  }

  @B("1")
  static class SingleBHaver {
  }

  @B("2")
  @B("3")
  static class ManyBHaver {
  }

  @Bs({@B("4"), @B("5")})
  static class BsHaver {
  }

  @Bs({@B("6"), @B("7")})
  @Bs({@B("8"), @B("9")})
  static class ManyBsHaver {
  }

  @Test void singleNonRepeatable() {
    A a =
      DIRECT.find(A.class, AHaver.class)
        .orElseGet(Assertions::fail);

    assertEquals(3, a.value());
  }

  @Test void singleRepeatable() {
    B b =
      DIRECT.find(B.class, SingleBHaver.class)
        .orElseGet(Assertions::fail);

    assertEquals("1", b.value());
  }

  @Test void singleRepeatableContainer() {
    DIRECT.find(Bs.class, SingleBHaver.class)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void manyRepeatable() {
    DIRECT.find(B.class, ManyBHaver.class)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void manyRepeatableContainer() {
    Bs bs =
      DIRECT.find(Bs.class, ManyBHaver.class)
        .orElseGet(Assertions::fail);

    assertEquals(2, bs.value().length);
  }

  @Test void singleInstanceOfRepeatableContainer() {
    Bs bs =
      DIRECT.find(Bs.class, BsHaver.class)
        .orElseGet(Assertions::fail);

    assertEquals(2, bs.value().length);
  }

  @Test void containerOfSingleRepeatableContainer() {
    DIRECT.find(Cs.class, BsHaver.class)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void manyRepeatableContainers() {
    DIRECT.find(Bs.class, ManyBsHaver.class)
      .ifPresent(AnnotationAssertions::falseFind);
  }

  @Test void containerOfManyRepeatableContainers() {
    Cs cs =
      DIRECT.find(Cs.class, ManyBsHaver.class)
        .orElseGet(Assertions::fail);

    assertEquals(2, cs.value().length);
  }
}
