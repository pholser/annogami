package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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
        Map<Class<? extends Annotation>, List<Annotation>> results =
            new HashMap<>();
        directOrIndirect.all(target).forEach(accumulateInto(results));

        if (target instanceof Class<?>) {
            for (Class<?> c = ((Class<?>) target).getSuperclass();
                c != null;
                c = c.getSuperclass()) {

                directOrIndirect.all(c)
                    .stream()
                    .filter(this::inherited)
                    .filter(a -> !results.containsKey(a.annotationType()))
                    .forEach(accumulateInto(results));
            }
        }

        return results.values()
            .stream()
            .flatMap(Collection::stream)
            .collect(toList());
    }

    private Consumer<Annotation> accumulateInto(
        Map<Class<? extends Annotation>, List<Annotation>> results) {

        return a ->
            results.computeIfAbsent(
                a.annotationType(),
                k -> new ArrayList<>())
                .add(a);
    }

    private boolean inherited(Annotation a) {
        return a.annotationType().getDeclaredAnnotation(Inherited.class)
            != null;
    }
}
