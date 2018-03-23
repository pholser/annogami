package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.*;

final class Annotations {
    static boolean containsRepeatableAnnotation(Annotation a) {
        return singleValueMethod(a.annotationType())
            .filter(Annotations::returnsRepeatableAnnotations)
            .isPresent();
    }

    static List<Annotation> repeatedAnnotationsOn(Annotation a) {
        Method method = singleValueMethod(a.annotationType())
            .filter(Annotations::returnsRepeatableAnnotations)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    a.annotationType() + " is not Repeatable"));

        try {
            Annotation[] repeated = (Annotation[]) method.invoke(a);
            return asList(repeated);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    private static Optional<Method> singleValueMethod(
        Class<? extends Annotation> annotationType) {

        Method[] methods = annotationType.getDeclaredMethods();
        return methods.length == 1 && "value".equals(methods[0].getName())
            ? Optional.of(methods[0])
            : Optional.empty();
    }

    private static boolean returnsRepeatableAnnotations(Method m) {
        return Optional.of(m.getReturnType())
            .filter(Class::isArray)
            .map(Class::getComponentType)
            .flatMap(Annotations::containerAnnotation)
            .filter(c -> c.equals(m.getDeclaringClass()))
            .isPresent();
    }

    private static Optional<Class<? extends Annotation>> containerAnnotation(
        Class<?> componentType) {

        return Optional.of(componentType)
            .filter(Class::isAnnotation)
            .map(c -> c.getDeclaredAnnotation(Repeatable.class))
            .map(Repeatable::value);
    }
}
