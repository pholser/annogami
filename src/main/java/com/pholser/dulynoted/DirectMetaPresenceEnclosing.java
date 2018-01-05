package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.function.Supplier;

class DirectMetaPresenceEnclosing {
    private final EnclosurePath enclosures;

    DirectMetaPresenceEnclosing(Method target) {
        enclosures = new MethodEnclosurePath(target);
    }

    DirectMetaPresenceEnclosing(Class<?> target) {
        enclosures = new ClassEnclosurePath(target);
    }

    DirectMetaPresenceEnclosing(Field target) {
        enclosures = new FieldEnclosurePath(target);
    }

    DirectMetaPresenceEnclosing(Parameter target) {
        enclosures = new ParameterEnclosurePath(target);
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return enclosures.stream()
            .map(e ->
                (Supplier<Optional<A>>) () ->
                    new DirectMetaPresence(e).find(annotationType))
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }
}
