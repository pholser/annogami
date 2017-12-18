package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

class DirectPresenceOnField {
    DirectPresenceOnField(Field field) {
    }

    <A extends Annotation> Optional<A> find(Class<A> annotationType) {
        return Optional.empty();
    }
}
