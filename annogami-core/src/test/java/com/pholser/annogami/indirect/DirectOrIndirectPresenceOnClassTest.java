package com.pholser.annogami.indirect;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectOrIndirectPresenceOnClassTest {
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
  @D(7)
  @D(8)
  static class Base {
  }

  static class Derived extends Base {
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

  @Test
  void findsDeclared() {
    assertThat(DIRECT_OR_INDIRECT.find(A.class, AHaver.class))
      .singleElement()
      .extracting(A::value)
      .isEqualTo(3);
  }

  @Test
  void missesNotDeclared() {
    assertThat(DIRECT_OR_INDIRECT.find(A.class, ManyBHaver.class)).isEmpty();
  }

  @Test
  void findsRepeatableDeclared() {
    assertThat(DIRECT_OR_INDIRECT.find(B.class, ManyBHaver.class))
      .extracting(B::value)
      .containsExactlyInAnyOrder(4, 5);
  }

  @Test
  void findsContainerForRepeatable() {
    assertThat(DIRECT_OR_INDIRECT.find(Bs.class, ManyBHaver.class))
      .singleElement()
      .satisfies(bs -> {
        assertThat(bs.value()).hasSize(2);
        assertThat(bs.value())
          .extracting(B::value)
          .containsExactlyInAnyOrder(4, 5);
      });
  }

  @Test
  void missesInheritedNonRepeatableOnSubclass() {
    assertThat(DIRECT_OR_INDIRECT.find(C.class, Derived.class)).isEmpty();
  }

  @Test
  void findsInheritedNonRepeatableOnBaseClassItself() {
    assertThat(DIRECT_OR_INDIRECT.find(C.class, Base.class))
      .singleElement()
      .extracting(C::value)
      .isEqualTo(6);
  }

  @Test
  void missesInheritedRepeatableOnSubclass() {
    assertThat(DIRECT_OR_INDIRECT.find(D.class, Derived.class)).isEmpty();
  }

  @Test
  void findsRepeatableDeclaredOnBaseClassItself() {
    assertThat(DIRECT_OR_INDIRECT.find(D.class, Base.class))
      .extracting(D::value)
      .containsExactlyInAnyOrder(7, 8);
  }

  @Test
  void missesInheritedContainerOnSubclass() {
    assertThat(DIRECT_OR_INDIRECT.find(Ds.class, Derived.class)).isEmpty();
  }

  @Test
  void seesContainerDeclaredOnBaseClassItself() {
    assertThat(DIRECT_OR_INDIRECT.find(Ds.class, Base.class))
      .singleElement()
      .satisfies(ds ->
        assertThat(ds.value())
          .extracting(D::value)
          .containsExactlyInAnyOrder(7, 8));
  }

  @Retention(RUNTIME)
  @interface Unused {
  }

  @Test
  void annotationTypeUnrelatedToTarget() {
    assertThat(DIRECT_OR_INDIRECT.find(Unused.class, AHaver.class)).isEmpty();
  }
}
