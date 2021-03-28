package com.pholser.dulynoted;

import com.pholser.dulynoted.annotations.Large;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Large
class AnnotationInvocationHandlerTest {
  private Large anno;
  private Large proxied;

  @BeforeEach void setup() {
    anno = getClass().getAnnotation(Large.class);

    proxied =
      (Large) Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class<?>[] { Large.class },
        new AnnotationInvocationHandler<>(
          Large.class,
          Reflection.defaultValues(Large.class)));
  }

  @Test void eq() {
    assertEquals(anno, proxied);
    assertEquals(proxied, anno);
  }

  @Test void hash() {
    assertEquals(anno.hashCode(), proxied.hashCode());
  }

  @Test void stringValue() {
    assertThat(
      proxied.toString(),
      startsWith("merged " + Large.class.getName()));
  }

  @Test void annoType() {
    assertEquals(Large.class, proxied.annotationType());
  }
}
