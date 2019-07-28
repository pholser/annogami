package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public final class AssociatedPresence
    implements AllByTypeDetector, AllDetector {

    private final DirectOrIndirectPresence directOrIndirect =
        new DirectOrIndirectPresence();

    @Override public <A extends Annotation> List<A> findAll(
        Class<A> annotationType,
        AnnotatedElement target) {

        return List.of(target.getAnnotationsByType(annotationType));
    }

    @Override public List<Annotation> all(AnnotatedElement target) {
        Map<Class<? extends Annotation>, List<Annotation>> results =
            new HashMap<>();
        directOrIndirect.all(target).forEach(accumulateInto(results));

        if (target instanceof Class<?>) {
            for (Class<?> c = ((Class<?>) target).getSuperclass();
                c != null;
                c = c.getSuperclass()) {

                Map<Class<? extends Annotation>, List<Annotation>> nextLevel =
                    new HashMap<>();
                directOrIndirect.all(c).forEach(accumulateInto(nextLevel));

                nextLevel.entrySet().stream()
                    .filter(e -> Annotations.inherited(e.getKey()))
                    .filter(e -> !results.containsKey(e.getKey()))
                    .forEach(mergeInto(results));
            }
        }

        return unmodifiableList(
            results.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(toList()));
    }

    private Consumer<Annotation> accumulateInto(
        Map<Class<? extends Annotation>, List<Annotation>> results) {

        return a ->
            results.computeIfAbsent(
                a.annotationType(),
                k -> new ArrayList<>())
                .add(a);
    }

    private Consumer<Map.Entry<Class<? extends Annotation>, List<Annotation>>>
        mergeInto(
            Map<Class<? extends Annotation>, List<Annotation>> results) {

        return e ->
            results.computeIfAbsent(
                e.getKey(),
                k -> new ArrayList<>()
            ).addAll(e.getValue());
    }
}
