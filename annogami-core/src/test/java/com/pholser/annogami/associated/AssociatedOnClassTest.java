package com.pholser.annogami.associated;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.ASSOCIATED;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class AssociatedOnClassTest {
  @Retention(RUNTIME)
  @interface A {
    int value();
  }

  @A(3)
  static class AHaver {
  }

  @Retention(RUNTIME)
  @Inherited
  @interface C {
    int value();
  }

  @C(6)
  static class CBase {
  }

  static class CDerived extends CBase {
  }

  @Retention(RUNTIME)
  @interface E {
    int value();
  }

  @E(9)
  static class ENonInheritedBase {
  }

  static class ENonInheritedDerived extends ENonInheritedBase {
  }

  @Retention(RUNTIME)
  @interface Bs {
    B[] value();
  }

  @Retention(RUNTIME)
  @Repeatable(Bs.class)
  @interface B {
    int value();
  }

  @B(4)
  @B(5)
  static class ManyBHaver {
  }

  @Retention(RUNTIME)
  @Inherited
  @interface Ds {
    D[] value();
  }

  @Retention(RUNTIME)
  @Repeatable(Ds.class)
  @interface D {
    int value();
  }

  @D(7)
  @D(8)
  static class DBase {
  }

  static class DDerived extends DBase {
  }

  @Test
  void findsSingleNonRepeatable() {
    assertThat(ASSOCIATED.find(A.class, AHaver.class))
      .singleElement()
      .extracting(A::value)
      .isEqualTo(3);
  }

  @Test
  void findsInheritedNonRepeatableOnSubclass() {
    assertThat(ASSOCIATED.find(C.class, CDerived.class))
      .singleElement()
      .extracting(C::value)
      .isEqualTo(6);
  }

  @Test
  void missesNonInheritedAnnotationOnSubclass() {
    List<E> es = ASSOCIATED.find(E.class, ENonInheritedDerived.class);

    assertThat(es).isEmpty();
  }

  @Test
  void findsRepeatableAnnotations() {
    List<B> bs = ASSOCIATED.find(B.class, ManyBHaver.class);

    assertThat(bs)
      .extracting(B::value)
      .containsExactlyInAnyOrder(4, 5);
  }

  @Test
  void findsContainerAnnotation() {
    assertThat(ASSOCIATED.find(Bs.class, ManyBHaver.class))
      .singleElement()
      .satisfies(bs -> assertThat(bs.value())
        .extracting(B::value)
        .containsExactlyInAnyOrder(4, 5));
  }

  @Test
  void findsInheritedContainerAnnotationOnSubclass() {
    assertThat(ASSOCIATED.find(Ds.class, DDerived.class))
      .singleElement()
      .satisfies(ds -> assertThat(ds.value())
        .extracting(D::value)
        .containsExactlyInAnyOrder(7, 8));
  }

  @Test
  void missesRepeatableAnnotationsOnSubclassViaInheritedContainer() {
    assertThat(ASSOCIATED.find(D.class, DDerived.class)).isEmpty();
  }
}
