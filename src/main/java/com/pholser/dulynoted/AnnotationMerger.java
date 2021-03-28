package com.pholser.dulynoted;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.pholser.dulynoted.Reflection.attributes;
import static java.util.Collections.emptySet;
import static java.util.Objects.deepEquals;

// Intended for sequential collection only.
class AnnotationMerger<A extends Annotation>
  implements Collector<A, Map<String, Object>, A> {

  private final Class<A> annoType;
  private final Map<String, Object> defaultValues;

  AnnotationMerger(Class<A> annoType) {
    this.annoType = annoType;
    this.defaultValues = Reflection.defaultValues(annoType);
  }

  @Override public Supplier<Map<String, Object>> supplier() {
    return () -> new HashMap<>(defaultValues);
  }

  @Override public BiConsumer<Map<String, Object>, A> accumulator() {
    return (attrs, anno) ->
      attributes(annoType, anno)
        .forEach((name, value) -> {
          Object defaultVal = defaultValues.get(name);
          if (deepEquals(attrs.get(name), defaultVal)
            && !deepEquals(value, defaultVal)) {

            attrs.put(name, value);
          }
        });
  }

  @Override public BinaryOperator<Map<String, Object>> combiner() {
    return (attrs1, attrs2) -> {
      throw new IllegalStateException(
        "annotation merging should not run in parallel");
    };
  }

  @Override public Function<Map<String, Object>, A> finisher() {
    return attrs ->
      annoType.cast(
        Proxy.newProxyInstance(
          annoType.getClassLoader(),
          new Class<?>[] { annoType },
          new AnnotationInvocationHandler<>(annoType, attrs)));
  }

  @Override public Set<Characteristics> characteristics() {
    return emptySet();
  }
}
