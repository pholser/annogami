# annogami Cookbook

Practical recipes for common annotation discovery patterns,
illustrated with Spring and JUnit use cases.

---

## Quick reference: choosing a presence

| Constant | Interface(s) | Scans | Meta-chain |
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

---

## Spring recipes

### Is a class a Spring component?

`@Service`, `@Repository`, `@Controller`, and `@RestController` are
all meta-annotated with `@Component`. To check whether a class
participates in component scanning:

```java
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;

boolean isComponent = !META_DIRECT_OR_INDIRECT
  .find(Component.class, MyService.class)
  .isEmpty();
```

`META_DIRECT_OR_INDIRECT` follows the meta-annotation chain from
annotations directly declared on `MyService.class`, finding
`@Component` however many levels deep it sits.

To also consider `@Component` arriving via an annotated superclass:

```java
import static com.pholser.annogami.Presences.META_ASSOCIATED;

boolean isComponent = !META_ASSOCIATED
  .find(Component.class, MyService.class)
  .isEmpty();
```

---

### HTTP mapping attribute synthesis

`@GetMapping`, `@PostMapping`, and friends are composed over
`@RequestMapping` using `@AliasFor`. With `Aliasing.spring()`,
annogami synthesizes a `@RequestMapping` proxy whose attributes
reflect the values declared on the composed annotation:

```java
import static com.pholser.annogami.Presences.META_DIRECT;

// method annotated with @GetMapping("/orders")
Optional<RequestMapping> mapping = META_DIRECT.find(
  RequestMapping.class, method, Aliasing.spring());

mapping.ifPresent(rm -> {
  // rm.path()   → ["/orders"]
  // rm.method() → [RequestMethod.GET]
});
```

`META_DIRECT` is `Single`: it returns the first (nearest) occurrence
in the meta chain. Use `META_DIRECT_OR_INDIRECT` if you expect the
annotation to appear more than once (e.g., repeatable mappings):

```java
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;

List<RequestMapping> mappings = META_DIRECT_OR_INDIRECT.find(
  RequestMapping.class, method, Aliasing.spring());
```

---

### Method-level `@Transactional` wins, class fills in defaults

Spring's convention is that a method-level `@Transactional` overrides
a class-level one. `AnnotatedPath.merge` captures this: the method is
earlier in the path, so its explicitly-set attributes win; the class
fills in any attribute the method left at its default.

```java
import static com.pholser.annogami.Presences.DIRECT;

Method method = OrderService.class.getMethod("placeOrder");

AnnotatedPath path = AnnotatedPathBuilder
  .fromMethod(method)
  .toDeclaringClass()
  .build();

// method's @Transactional attributes win;
// class-level @Transactional fills the rest
Optional<Transactional> tx = path.merge(
  Transactional.class, DIRECT);
```

With a composed `@Transactional` variant (using `@AliasFor`):

```java
Optional<Transactional> tx = path.merge(
  Transactional.class, DIRECT, Aliasing.spring());
```

---

### `@Transactional` through the type hierarchy

To find `@Transactional` on a method or its declaring class,
following superclasses and interfaces (for class-level annotations
marked `@Inherited`):

```java
import static com.pholser.annogami.Presences.META_ASSOCIATED;

List<Transactional> txList = META_ASSOCIATED.find(
  Transactional.class, method, Aliasing.spring());
```

`META_ASSOCIATED` walks both the meta-annotation chain and the class
hierarchy, so it finds `@Transactional` whether it appears directly on
the method, on the declaring class, on a superclass, or through a
composed annotation at any of those levels.

---

### All annotations on an element, meta-chain included

To get a flat list of every annotation visible from a program element
— including annotations on those annotations — with aliasing applied:

```java
import static com.pholser.annogami.Presences.META_DIRECT;

List<Annotation> all = META_DIRECT.all(
  MyController.class, Aliasing.spring());
```

Use `META_PRESENT` instead to also include annotations arriving via
`@Inherited` from superclasses.

---

## JUnit recipes

### Test extension discovery

JUnit's `@ExtendWith` is commonly buried inside composed annotations.
`@SpringBootTest`, for example, meta-annotates
`@ExtendWith(SpringExtension.class)`. To collect all extensions
applicable to a test class:

```java
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;

List<ExtendWith> extensions = META_DIRECT_OR_INDIRECT.find(
  ExtendWith.class, MyTest.class);
```

To also pick up extensions declared on a superclass:

```java
import static com.pholser.annogami.Presences.META_ASSOCIATED;

List<ExtendWith> extensions = META_ASSOCIATED.find(
  ExtendWith.class, MyTest.class);
```

---

### Composed test annotations

Given a composed annotation that bundles test metadata:

```java
@Target(METHOD) @Retention(RUNTIME)
@Test
@Tag("fast")
@Tag("unit")
public @interface FastUnitTest {}
```

To find all `@Tag` values on a method annotated `@FastUnitTest`:

```java
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;

List<Tag> tags = META_DIRECT_OR_INDIRECT.find(
  Tag.class, method);
// → [Tag("fast"), Tag("unit")]
```

This handles both direct `@Tag` annotations and `@Tag` arriving
through any depth of composed annotations.

---

### Is this a `@Test` method?

For a single method, direct check:

```java
import static com.pholser.annogami.Presences.DIRECT;

boolean isTest = DIRECT.find(Test.class, method).isPresent();
```

To also find `@Test` arriving through a composed annotation
(e.g., `@FastUnitTest` above):

```java
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;

boolean isTest = !META_DIRECT_OR_INDIRECT
  .find(Test.class, method)
  .isEmpty();
```

---

### `@BeforeEach` through the method override hierarchy

JUnit 5 discovers `@BeforeEach` on superclass methods, but if you
are building your own discovery logic and want to follow overrides
explicitly:

```java
import static com.pholser.annogami.Presences.DIRECT;

Method method = MyTest.class.getMethod("setUp");

AnnotatedPath path = AnnotatedPathBuilder
  .fromMethod(method)
  .toDepthOverridden()
  .build();

// present on the most-specific override, or any ancestor
Optional<BeforeEach> beforeEach =
  path.findFirst(BeforeEach.class, DIRECT);
```

`toDepthOverridden()` builds a path through all methods in the class
hierarchy that the starting method overrides, depth-first (superclass
before interfaces).

---

## Combining path and aliasing

The recipes above compose freely. For example, to find
`@RequestMapping` (synthesized from any composed mapping annotation)
considering the full method override hierarchy:

```java
import static com.pholser.annogami.Presences.META_DIRECT_OR_INDIRECT;

Method method = MyController.class.getMethod("handleRequest");

AnnotatedPath path = AnnotatedPathBuilder
  .fromMethod(method)
  .toDepthOverridden()
  .build();

List<RequestMapping> mappings = path.find(
  RequestMapping.class,
  META_DIRECT_OR_INDIRECT,
  Aliasing.spring());
```

The first element in `mappings` is from the most-specific override;
later elements are from overridden ancestors. Use
`path.findFirst(RequestMapping.class, META_DIRECT_OR_INDIRECT,
Aliasing.spring())` if you only want the most-specific mapping.
