package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

class DirectMetaPresence {
    private final AnnotatedElement target;

    DirectMetaPresence(AnnotatedElement target) {
        this.target = target;
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return find(annotationType, new HashSet<>());
    }

    private <A extends Annotation> Optional<A> find(
        Class<A> annotationType,
        Set<Annotation> seen) {

        Supplier<Optional<A>> root =
            () -> new DirectPresence(target).find(annotationType);

        Stream<Supplier<Optional<A>>> metas =
            Arrays.stream(target.getDeclaredAnnotations())
                .filter(seen::add)
                .map(Annotation::annotationType)
                .filter(excludes().negate())
                .map(t -> () -> new DirectMetaPresence(t).find(annotationType, seen));

        return Stream.concat(Stream.of(root), metas)
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    private Predicate<Class<? extends Annotation>> excludes() {
        return c -> c.getName().startsWith("java.lang.annotation.")
            || c.getName().startsWith("kotlin.");
    }
}
