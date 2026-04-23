package com.pholser.annogami.programmatic;

import com.pholser.annogami.Aliasing;
import com.pholser.annogami.SynthesizedAnnotations;
import com.pholser.annogami.internal.AnnotationInvoker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@link Aliasing} implementation whose alias relationships are declared
 * programmatically rather than derived from meta-annotations on the annotation
 * types themselves.
 *
 * <p>This is useful when the annotation types cannot be modified — for example,
 * when bridging annotations from two independent frameworks.
 *
 * <p>Build an instance with {@link #builder()}:
 * <pre>{@code
 * Aliasing aliasing = ProgrammaticAliasing.builder()
 *   .alias(GetMapping.class, "value", Route.class, "path")
 *   .alias(GetMapping.class, "path",  Route.class, "path")
 *   .build();
 * }</pre>
 *
 * <p>Each {@code alias} call declares a directed edge: when synthesizing the
 * target annotation type, the value of the source attribute is used to supply
 * the target attribute (if the source value is non-default). When multiple
 * sources are registered for the same target attribute, the first one whose
 * value is non-default wins.
 */
public final class ProgrammaticAliasing implements Aliasing {
  private final Map<Class<? extends Annotation>, Map<String, List<SourceRef>>> edgesByTarget;

  private ProgrammaticAliasing(
    Map<Class<? extends Annotation>, Map<String, List<SourceRef>>> edgesByTarget) {

    this.edgesByTarget = edgesByTarget;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public <A extends Annotation> Optional<A> synthesize(
    Class<A> annoType,
    List<Annotation> metaContext) {

    Objects.requireNonNull(annoType, "annoType");
    Objects.requireNonNull(metaContext, "metaContext");

    Map<String, List<SourceRef>> attrEdges = edgesByTarget.get(annoType);
    if (attrEdges == null || attrEdges.isEmpty()) {
      return Optional.empty();
    }

    Map<Class<? extends Annotation>, Annotation> contextByType = new LinkedHashMap<>();
    for (Annotation a : metaContext) {
      contextByType.putIfAbsent(a.annotationType(), a);
    }

    Map<String, Object> overrides = new LinkedHashMap<>();

    for (Map.Entry<String, List<SourceRef>> entry : attrEdges.entrySet()) {
      String targetAttr = entry.getKey();
      for (SourceRef source : entry.getValue()) {
        Annotation sourceAnnotation = contextByType.get(source.sourceType());
        if (sourceAnnotation == null) {
          continue;
        }

        Object value = invoke(sourceAnnotation, source.sourceAttr());
        Object defaultValue = defaultValueOf(source.sourceType(), source.sourceAttr());

        if (!Objects.deepEquals(value, defaultValue)) {
          overrides.put(targetAttr, value);
          break;
        }
      }
    }

    if (overrides.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(SynthesizedAnnotations.of(annoType, overrides));
  }

  private static Object invoke(Annotation annotation, String attrName) {
    try {
      Method m = annotation.annotationType().getDeclaredMethod(attrName);
      return AnnotationInvoker.invoke(annotation, m, () -> m.invoke(annotation));
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(
        "Cannot invoke " + annotation.annotationType().getName() + "." + attrName + "()", e);
    }
  }

  private static Object defaultValueOf(
    Class<? extends Annotation> annoType,
    String attrName) {

    try {
      return annoType.getDeclaredMethod(attrName).getDefaultValue();
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(
        "No attribute '" + attrName + "' on " + annoType.getName(), e);
    }
  }

  private record SourceRef(
    Class<? extends Annotation> sourceType,
    String sourceAttr) {
  }

  public static final class Builder {
    private final Map<Class<? extends Annotation>, Map<String, List<SourceRef>>>
      edgesByTarget = new LinkedHashMap<>();

    private Builder() {
    }

    /**
     * Declares that when synthesizing {@code targetType}, the value of
     * {@code sourceType.sourceAttr} should supply {@code targetType.targetAttr}.
     *
     * @throws IllegalArgumentException if either attribute does not exist on its
     *         annotation type, or if their return types are incompatible
     */
    public <S extends Annotation, T extends Annotation> Builder alias(
      Class<S> sourceType,
      String sourceAttr,
      Class<T> targetType,
      String targetAttr) {

      Objects.requireNonNull(sourceType, "sourceType");
      Objects.requireNonNull(sourceAttr, "sourceAttr");
      Objects.requireNonNull(targetType, "targetType");
      Objects.requireNonNull(targetAttr, "targetAttr");

      if (sourceType == targetType && sourceAttr.equals(targetAttr)) {
        throw new IllegalArgumentException(
          "Self-alias: " + sourceType.getName() + "." + sourceAttr + " cannot alias itself");
      }

      Method sourceMethod = resolveAttr(sourceType, sourceAttr);
      Method targetMethod = resolveAttr(targetType, targetAttr);

      if (sourceMethod.getDefaultValue() == null) {
        throw new IllegalArgumentException(
          "Source attribute " + sourceType.getName() + "." + sourceAttr
            + "() has no default value; aliasing requires a default");
      }
      if (targetMethod.getDefaultValue() == null) {
        throw new IllegalArgumentException(
          "Target attribute " + targetType.getName() + "." + targetAttr
            + "() has no default value; aliasing requires a default");
      }

      if (!sourceMethod.getReturnType().equals(targetMethod.getReturnType())) {
        throw new IllegalArgumentException(
          "Incompatible attribute types for alias "
            + sourceType.getName() + "." + sourceAttr + "() ("
            + sourceMethod.getReturnType().getName() + ") -> "
            + targetType.getName() + "." + targetAttr + "() ("
            + targetMethod.getReturnType().getName() + "): incompatible return types");
      }

      edgesByTarget
        .computeIfAbsent(targetType, k -> new LinkedHashMap<>())
        .computeIfAbsent(targetAttr, k -> new ArrayList<>())
        .add(new SourceRef(sourceType, sourceAttr));

      return this;
    }

    public ProgrammaticAliasing build() {
      Map<Class<? extends Annotation>, Map<String, List<SourceRef>>> copy =
        new LinkedHashMap<>();
      for (var outer : edgesByTarget.entrySet()) {
        Map<String, List<SourceRef>> attrMap = new LinkedHashMap<>();
        for (var inner : outer.getValue().entrySet()) {
          attrMap.put(inner.getKey(), List.copyOf(inner.getValue()));
        }
        copy.put(outer.getKey(), Map.copyOf(attrMap));
      }
      return new ProgrammaticAliasing(Map.copyOf(copy));
    }

    private static Method resolveAttr(
      Class<? extends Annotation> annoType,
      String attrName) {

      try {
        Method m = annoType.getDeclaredMethod(attrName);
        if (m.getParameterCount() != 0 || m.getReturnType() == void.class) {
          throw new IllegalArgumentException(
            "'" + attrName + "' on " + annoType.getName()
              + " is not a valid annotation attribute");
        }
        return m;
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException(
          "Attribute '" + attrName + "' not found on " + annoType.getName(), e);
      }
    }
  }
}
