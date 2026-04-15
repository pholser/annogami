# annogami

A Java library for annotation discovery with explicit scanning scope,
attribute synthesis (Spring `@AliasFor`), and path-based attribute merging.

This repository also contains a compile-time annotation processor that
statically validates Spring `@AliasFor` usage.

---

## Modules

| Module | Description |
|---|---|
| `annogami` | Annotation discovery library |
| `aliasfor-processor-core` | Compile-time `@AliasFor` validation processor |
| `aliasfor-processor-tests` | Tests for the processor |

---

## annogami

### Presence constants

All discovery operations start by choosing a *presence constant* from
`Presences`. The constant encodes two orthogonal decisions: whether to
traverse the meta-annotation chain, and whether to follow `@Inherited`
annotations up the class hierarchy.

| Constant | Interfaces | Scans | Meta-chain |
|---|---|---|---|
| `DIRECT` | `Single`, `All` | declared only | no |
| `PRESENT` | `Single`, `All` | declared + `@Inherited` | no |
| `META_DIRECT` | `Single`, `All` | declared only | yes |
| `META_PRESENT` | `Single`, `All` | declared + `@Inherited` | yes |
| `DIRECT_OR_INDIRECT` | `AllByType` | declared only | yes |
| `ASSOCIATED` | `AllByType` | declared + `@Inherited` | yes |
| `META_DIRECT_OR_INDIRECT` | `AllByType` | declared only | yes |
| `META_ASSOCIATED` | `AllByType` | declared + `@Inherited` | yes |

`Single` returns `Optional<A>`; `All` returns `List<Annotation>`;
`AllByType` returns `List<A>`.

```java
import static com.pholser.annogami.Presences.*;

// Is MyService annotated with @Component or something meta-annotated with it?
boolean isComponent =
  !META_PRESENT.find(Component.class, MyService.class).isEmpty();

// Find @RequestMapping synthesized from @GetMapping (with @AliasFor support)
Optional<RequestMapping> rm =
  META_DIRECT.find(RequestMapping.class, method, Aliasing.spring());
```

### Aliasing

`Aliasing.spring()` enables Spring `@AliasFor` attribute synthesis. Pass
it as the final argument to any `find` or `all` call:

```java
Optional<RequestMapping> rm =
  META_DIRECT.find(RequestMapping.class, method, Aliasing.spring());

List<Annotation> all =
  META_DIRECT.all(element, Aliasing.spring());
```

Without an `Aliasing` argument, annotations are returned as-is from the
JVM with no attribute overriding.

### Annotated paths

An `AnnotatedPath` is an ordered sequence of `AnnotatedElement`s where
elements earlier in the path take priority over later ones. Build paths
with `AnnotatedPathBuilder`.

```java
// Method first, then its declaring class
AnnotatedPath path =
  AnnotatedPathBuilder.fromMethod(method)
    .toDeclaringClass()
    .build();

// Most-specific override first, then all overridden superclass methods
AnnotatedPath overridePath =
  AnnotatedPathBuilder.fromMethod(method)
    .toDepthOverridden()
    .build();

// A class followed by its full type hierarchy
AnnotatedPath hierarchyPath =
  AnnotatedPathBuilder.fromClass(MyClass.class)
    .toDepthHierarchy()
    .build();
```

Other starting points: `fromParameter`, `fromField`, `fromConstructor`,
`fromRecordComponent`.

Other extensions from a class segment: `toDeclaringPackage`,
`toDeclaringModule`, `toEnclosingMethod`, `toClassEnclosure`,
`toBreadthHierarchy`.

### Path operations

```java
// First occurrence of @Transactional in the path
Optional<Transactional> tx =
  path.findFirst(Transactional.class, DIRECT);

// All occurrences, in path order (method first, then class)
List<Transactional> all =
  path.find(Transactional.class, DIRECT_OR_INDIRECT);

// Per-attribute merge: first non-default value wins per attribute;
// later path elements fill in attributes still at their defaults
Optional<Transactional> merged =
  path.merge(Transactional.class, DIRECT);

// All annotations from all elements in the path
List<Annotation> everything =
  path.all(META_DIRECT, Aliasing.spring());
```

`merge` works with `Single` only (no `All`/`AllByType` overload). This
mirrors the constraint that a meta-annotation chain contains at most one
instance of a given annotation type.

### Spring comparison

Spring's `AnnotatedElementUtils.findMergedAnnotation` finds the nearest
single `@Transactional` and returns it as a whole. Attributes at their
default on that instance are not filled in from lower-priority instances.

annogami's `AnnotatedPath.merge` fills in per-attribute: the first
non-default value anywhere in the path wins for that attribute, and later
path elements contribute to attributes still at their defaults.

```java
// Spring: method-level readOnly=true wins, but timeout stays at -1
//         because the method annotation is returned as-is
Transactional spring =
  AnnotatedElementUtils.findMergedAnnotation(method, Transactional.class);
// spring.readOnly() == true, spring.timeout() == -1

// annogami: readOnly=true from method, timeout=30 filled from class
Optional<Transactional> annogami =
  path.merge(Transactional.class, DIRECT);
// annogami.get().readOnly() == true, annogami.get().timeout() == 30
```

See [`docs/cookbook.md`](docs/cookbook.md) for more worked examples comparing
annogami with Spring's `AnnotatedElementUtils`/`MergedAnnotations` and
JUnit's `AnnotationSupport`.

---

## aliasfor-processor

`AliasForValidationProcessor` is a `javax.annotation.processing` processor
that statically validates Spring `@AliasFor` usage at compile time. It does
not depend on Spring at compile time — it refers to
`org.springframework.core.annotation.AliasFor` by name only.

**What it checks:**

- `@AliasFor` must appear only on annotation attribute methods
- The source attribute and the target attribute must have the same return type
- Both must declare default values, and those defaults must be equal
- For cross-annotation aliases, the declaring annotation must be
  meta-annotated with the target annotation
- Usages: intra-annotation aliased attributes set to conflicting explicit
  values are reported as errors

**Usage:**

Add `aliasfor-processor-core` to your annotation processor classpath.
With Gradle:

```groovy
annotationProcessor project(':aliasfor-processor-core')
```

Or as a standalone JAR on `javac`'s `-processorpath`.

---

## Requirements

- Java 17+
- Spring `spring-core` on the classpath for `Aliasing.spring()` support
  (the `annogami` library itself has no mandatory Spring dependency)

---

## Status

Early development. APIs are not yet stable.
