package com.pholser.annogami.present;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.PRESENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class PresentOnClassTest {
  @Retention(RUNTIME)
  @interface A {
    int value();
  }

  @A(3)
  static class AHaver {
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
    assertThat(PRESENT.find(A.class, AHaver.class))
      .hasValueSatisfying(a ->
        assertThat(a.value()).isEqualTo(3));
  }

  @Test
  void missesNonDeclaredNonRepeatable() {
    assertThat(PRESENT.find(A.class, ManyBHaver.class)).isEmpty();
  }

  @Test
  void missesRepeatableWhenOnlyContainerIsPresent() {
    assertThat(PRESENT.find(B.class, ManyBHaver.class)).isEmpty();
  }

  @Test
  void findsContainerAnnotationForRepeatable() {
    assertThat(PRESENT.find(Bs.class, ManyBHaver.class))
      .hasValueSatisfying(bs ->
        assertThat(bs.value())
          .extracting(B::value)
          .containsExactlyInAnyOrder(4, 5));
  }

  @Test
  void findsInheritedNonRepeatableOnSubclass() {
    assertThat(PRESENT.find(C.class, CDerived.class))
      .hasValueSatisfying(c ->
        assertThat(c.value()).isEqualTo(6));
  }

  @Test
  void findsDirectAnnotationOnBaseClassItself() {
    assertThat(PRESENT.find(C.class, CBase.class))
      .hasValueSatisfying(c ->
        assertThat(c.value()).isEqualTo(6));
  }

  @Test
  void doesNotInheritNonInheritedAnnotationToSubclass() {
    assertThat(PRESENT.find(E.class, ENonInheritedDerived.class)).isEmpty();

    assertThat(PRESENT.find(E.class, ENonInheritedBase.class))
      .hasValueSatisfying(base ->
        assertThat(base.value()).isEqualTo(9));
  }

  @Test
  void inheritedContainerAnnotationIsPresentOnSubclass() {
    assertThat(PRESENT.find(Ds.class, DDerived.class))
      .hasValueSatisfying(dsOnDerived ->
        assertThat(dsOnDerived.value())
          .extracting(D::value)
          .containsExactlyInAnyOrder(7, 8));
  }

  @Test
  void elementTypeOfRepeatableIsNotItselfInherited() {
    assertThat(PRESENT.find(D.class, DDerived.class)).isEmpty();

    assertThat(PRESENT.find(D.class, DBase.class)).isEmpty();

    assertThat(PRESENT.find(Ds.class, DBase.class))
      .hasValueSatisfying(dsOnBase ->
        assertThat(dsOnBase.value())
          .extracting(D::value)
          .containsExactlyInAnyOrder(7, 8));
  }

  @Test
  void findsAllAnnotations() {
    assertThat(PRESENT.all(AHaver.class))
      .extracting(a -> a.annotationType().getName())
      .containsExactlyInAnyOrder(A.class.getName());
  }

  @Test
  void findsAllRepeatableContainerNotRepeatableElements() {
    assertThat(PRESENT.all(ManyBHaver.class))
      .extracting(a -> a.annotationType().getName())
      .containsExactly(Bs.class.getName())
      .doesNotContain(B.class.getName());
  }

  @Test
  void findsAllInheritedNonRepeatable() {
    assertThat(PRESENT.all(CDerived.class))
      .extracting(a -> a.annotationType().getName())
      .containsExactly(C.class.getName());
  }

  @Test
  void missesAllNonInheritedOnSubclass() {
    assertThat(PRESENT.all(ENonInheritedDerived.class))
      .extracting(a -> a.annotationType().getName())
      .doesNotContain(E.class.getName());
  }

  @Test
  void findsAllInheritedContainerOnSubclassWhenContainerIsInherited() {
    assertThat(PRESENT.all(DDerived.class))
      .extracting(a -> a.annotationType().getName())
      .containsExactly(Ds.class.getName())
      .doesNotContain(D.class.getName());
  }
}
