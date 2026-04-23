package com.pholser.annogami.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.Callable;

/**
 * Shared helper for reflectively invoking annotation attribute methods across
 * module boundaries.
 *
 * <p>All JVM annotation instances are {@link Proxy} objects backed by an
 * internal {@code AnnotationInvocationHandler} in {@code java.base}.
 * Dispatching through the proxy's {@link InvocationHandler} lets the call
 * execute in {@code java.base}'s module context, which has access to
 * annotation types in any package — including packages that are only
 * qualified-exported and do not list the calling annogami module.
 *
 * <p>For non-proxy annotations (custom implementations), the direct
 * invocation requires the calling module to have access to the annotation
 * type. Callers supply this as a {@code fallback} lambda so that it executes
 * in the calling module's access context rather than this module's.
 */
public final class AnnotationInvoker {
  private AnnotationInvoker() {
    throw new AssertionError();
  }

  /**
   * Invokes the given annotation attribute method on {@code annotation}.
   *
   * <p>If {@code annotation} is a JVM proxy, the call is dispatched through
   * its {@link InvocationHandler} so that it executes in {@code java.base}'s
   * module context. Otherwise, {@code fallback} is called — it should perform
   * the direct invocation and is typically the lambda
   * {@code () -> attr.invoke(annotation)}, which executes in the calling
   * module's access context.
   *
   * @param annotation the annotation instance to invoke on
   * @param attr the zero-arg attribute method to invoke
   * @param fallback invoked when {@code annotation} is not a JVM proxy;
   * executes in the calling module's access context
   * @return the attribute value
   * @throws IllegalStateException if the invocation fails
   */
  public static Object invoke(
    Annotation annotation,
    Method attr,
    Callable<Object> fallback) {

    if (Proxy.isProxyClass(annotation.getClass())) {
      InvocationHandler h = Proxy.getInvocationHandler(annotation);

      try {
        return h.invoke(annotation, attr, null);
      } catch (RuntimeException | Error e) {
        throw e;
      } catch (Throwable t) {
        throw new UndeclaredThrowableException(t);
      }
    }

    try {
      return fallback.call();
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException(
        "Cannot invoke " + annotation.annotationType().getName()
          + "." + attr.getName() + "()", e);
    }
  }
}
