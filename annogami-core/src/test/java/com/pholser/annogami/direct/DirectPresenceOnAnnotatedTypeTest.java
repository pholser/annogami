package com.pholser.annogami.direct;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.List;

import static com.pholser.annogami.Presences.DIRECT;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

class DirectPresenceOnAnnotatedTypeTest {
  @Retention(RUNTIME)
  @Target(TYPE_USE)
  @interface A {
    int value();
  }

  @Retention(RUNTIME)
  @Target(TYPE_USE)
  @interface Bs {
    B[] value();
  }

  @Retention(RUNTIME)
  @Target(TYPE_USE)
  @Repeatable(Bs.class)
  @interface B {
    int value();
  }

  static class Holder {
    int @A(1) [] array;
    int @B(2) @B(3) [] repeatArray;
    @A(4)
    List<String> param;
    @B(5)
    @B(6)
    List<String> repeatParam;
    List<@A(7) String> annotatedArg;
    List<String> plain;
  }

  @Test
  void findsOnAnnotatedArrayType() throws Exception {
    assertThat(
      DIRECT.find(
        A.class,
        Holder.class.getDeclaredField("array").getAnnotatedType()))
      .isPresent()
      .hasValueSatisfying(a -> assertThat(a.value()).isEqualTo(1));
  }

  @Test
  void missesOnAnnotatedArrayTypeNotDeclared() throws Exception {
    assertThat(DIRECT.find(
      A.class,
      Holder.class.getDeclaredField("plain").getAnnotatedType()
    )).isEmpty();
  }

  @Test
  void missesOnAnnotatedArrayTypeIndirectlyPresent() throws Exception {
    assertThat(DIRECT.find(
      B.class,
      Holder.class.getDeclaredField("repeatArray").getAnnotatedType()
    )).isEmpty();
  }

  @Test
  void findsContainerOnAnnotatedArrayType() throws Exception {
    assertThat(
      DIRECT.find(
        Bs.class,
        Holder.class.getDeclaredField("repeatArray").getAnnotatedType()))
      .isPresent()
      .hasValueSatisfying(bs -> assertThat(bs.value()).hasSize(2));
  }

  @Test
  void findsOnAnnotatedParameterizedType() throws Exception {
    assertThat(
      DIRECT.find(
        A.class,
        Holder.class.getDeclaredField("param").getAnnotatedType()))
      .isPresent()
      .hasValueSatisfying(a -> assertThat(a.value()).isEqualTo(4));
  }

  @Test
  void findsContainerOnAnnotatedParameterizedType() throws Exception {
    assertThat(
      DIRECT.find(
        Bs.class,
        Holder.class.getDeclaredField("repeatParam").getAnnotatedType()))
      .isPresent()
      .hasValueSatisfying(bs -> assertThat(bs.value()).hasSize(2));
  }

  @Test
  void findsOnAnnotatedTypeArgument() throws Exception {
    Field f = Holder.class.getDeclaredField("annotatedArg");
    AnnotatedParameterizedType paramType =
      (AnnotatedParameterizedType) f.getAnnotatedType();
    AnnotatedType argType = paramType.getAnnotatedActualTypeArguments()[0];

    assertThat(DIRECT.find(A.class, argType))
      .isPresent()
      .hasValueSatisfying(a -> assertThat(a.value()).isEqualTo(7));
  }
}
