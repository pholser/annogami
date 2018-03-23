package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public final class AssociatedPresence implements Detector {
    private final DirectOrIndirectPresence directOrIndirect =
        new DirectOrIndirectPresence();

    @Override
    public <A extends Annotation> Optional<A> find(
        Class<A> annotationType,
        AnnotatedElement target) {

        A[] annotations = target.getAnnotationsByType(annotationType);
        return annotations.length == 1
            ? Optional.of(annotations[0])
            : Optional.empty();
    }

    @Override
    public <A extends Annotation> List<A> findAll(
        Class<A> annotationType,
        AnnotatedElement target) {

        List<A> results = new ArrayList<>();
        Collections.addAll(
            results,
            target.getAnnotationsByType(annotationType));

        return results;
    }

    @Override
    public List<Annotation> all(AnnotatedElement target) {
        List<Annotation> roots = directOrIndirect.all(target);
        List<Annotation> results = new ArrayList<>(roots);

        if (target instanceof Class<?>) {
            for (Class<?> c = ((Class<?>) target).getSuperclass();
                c != null;
                c = c.getSuperclass()) {

                directOrIndirect.all(c).stream()
                    .filter(this::inherited)
                    .forEach(results::add);
            }
        }

        return results;
    }

    private boolean inherited(Annotation a) {
        return a.annotationType().getDeclaredAnnotation(Inherited.class)
            != null;
    }
}
