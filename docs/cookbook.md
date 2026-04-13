# annogami Cookbook

Practical recipes for common annotation discovery patterns, illustrated with
Spring and JUnit use cases. Each recipe shows the equivalent call using the
framework's own utilities, then the annogami alternative, and notes where the
two differ in behavior.

---

## Quick reference: choosing a presence

| Constant                  | Interface(s)    | Scans                   | Meta-chain |
|---------------------------|-----------------|-------------------------|------------|
| `DIRECT`                  | `Single`, `All` | declared only           | no         |
| `PRESENT`                 | `Single`, `All` | declared + `@Inherited` | no         |
| `META_DIRECT`             | `Single`, `All` | declared only           | yes        |
| `META_PRESENT`            | `Single`, `All` | declared + `@Inherited` | yes        |
| `DIRECT_OR_INDIRECT`      | `AllByType`     | declared only           | yes        |
| `ASSOCIATED`              | `AllByType`     | declared + `@Inherited` | yes        |
| `META_DIRECT_OR_INDIRECT` | `AllByType`     | declared only           | yes        |
| `META_ASSOCIATED`         | `AllByType`     | declared + `@Inherited` | yes        |

`Single` returns `Optional<A>`; `All` returns `List<Annotation>`; `AllByType`
returns `List<A>`.

---

## Spring recipes

### Is a class a Spring component?

`@Service`, `@Repository`, `@Controller`, and `@RestController` are all
meta-annotated with `@Component`.

**Spring (`AnnotatedElementUtils`):**

```java
boolean isComponent =
  AnnotatedElementUtils.hasAnnotation(MyService.class, Component.class);
```

**Spring (`MergedAnnotations`, 5.2+):**

```java
boolean isComponent =
  MergedAnnotations.from(MyService.class, SearchStrategy.TYPE_HIERARCHY)
    .isPresent(Component.class);
```

**annogami:**

```java
// declared annotations only — no superclass inheritance
boolean isComponent =
  !META_DIRECT_OR_INDIRECT.find(Component.class, MyService.class)
    .isEmpty();

// also considers @Component on annotated superclasses
boolean isComponent =
  !META_ASSOCIATED.find(Component.class, MyService.class)
    .isEmpty();
```

`META_ASSOCIATED` corresponds most closely to `SearchStrategy.TYPE_HIERARCHY`:
it walks both the meta-annotation chain and the class hierarchy. The scanning
strategy is an explicit choice in annogami rather than a flag passed at call
time.

---

### HTTP mapping attribute synthesis

`@GetMapping`, `@PostMapping`, and friends are composed over `@RequestMapping`
via `@AliasFor`.

**Spring (`AnnotatedElementUtils`):**

```java
// returns null if not found; synthesized proxy if found
RequestMapping rm =
  AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
```

**Spring (`MergedAnnotations`, 5.2+):**

```java
// throws NoSuchElementException if not present
RequestMapping rm =
  MergedAnnotations.from(method)
    .get(RequestMapping.class)
    .synthesize();

// safe form
MergedAnnotation<RequestMapping> merged =
  MergedAnnotations.from(method)
    .get(RequestMapping.class);
if(merged.

isPresent()){
RequestMapping rm = merged.synthesize();
}
```

**annogami:**

```java
// META_DIRECT is Single — Optional, never null
Optional<RequestMapping> rm =
  META_DIRECT.find(
    RequestMapping.class, method, Aliasing.spring());

// all occurrences (e.g. repeatable mappings)
List<RequestMapping> rms =
  META_DIRECT_OR_INDIRECT.find(
    RequestMapping.class, method, Aliasing.spring());
```

annogami returns `Optional` rather than `null`, making absence explicit.
Spring's `MergedAnnotations` is lazy (synthesizes on access); annogami
synthesizes eagerly when `find` is called.

---

### Method-level `@Transactional` wins, class fills in defaults

**Spring (`AnnotatedElementUtils`):**

```java
// finds nearest whole @Transactional: method first,
// then declaring class, then hierarchy — returns it as-is,
// no attribute fill-in from lower-priority instances
Transactional tx =
  AnnotatedElementUtils.findMergedAnnotation(method, Transactional.class);
```

**annogami:**

```java
Method method = OrderService.class.getMethod("placeOrder");

AnnotatedPath path =
  AnnotatedPathBuilder.fromMethod(method)
    .toDeclaringClass()
    .build();

// method's @Transactional attributes win per attribute;
// class-level @Transactional fills in any still at default
Optional<Transactional> tx =
  path.merge(Transactional.class, DIRECT);

// with composed @Transactional variants
Optional<Transactional> tx =
  path.merge(Transactional.class, DIRECT, Aliasing.spring());
```

This is a genuine behavioral difference. Spring's `findMergedAnnotation` finds
the nearest `@Transactional` instance and returns it whole -- if that instance
has attributes at their defaults, those defaults stand even if a lower-priority
instance sets them explicitly. annogami's `merge` fills in
attribute-by-attribute: for each attribute, the first path element with a
non-default value wins, and later elements contribute values for attributes
still at their defaults. The path ordering (method before class) is explicit
rather than implicit in a search strategy.

---

### `@Transactional` through the type hierarchy

**Spring (`AnnotatedElementUtils`):**

```java
// searches method, then declaring class, then hierarchy;
// returns the first (nearest) match
Transactional tx =
  AnnotatedElementUtils
    .findMergedAnnotation(method, Transactional.class);
```

**annogami:**

```java
// returns all matches in discovery order
List<Transactional> txList =
  META_ASSOCIATED.find(
    Transactional.class, method, Aliasing.spring());
```

Spring returns a single merged annotation; annogami returns every occurrence,
in order, giving the caller visibility into all of them.
Use `META_ASSOCIATED.find(...).stream().findFirst()` to replicate Spring's
"nearest wins" behaviour.

---

### All annotations on an element, meta-chain included

**Spring (`MergedAnnotations`, 5.2+):**

```java
List<Annotation> all =
  MergedAnnotations
    .from(element, SearchStrategy.TYPE_HIERARCHY)
    .stream()
    .filter(MergedAnnotation::isPresent)
    .map(MergedAnnotation::synthesize)
    .toList();
```

**annogami:**

```java
// declared annotations only, meta-chain traversed
List<Annotation> all =
  META_DIRECT.all(element, Aliasing.spring());

// also includes annotations from superclasses
List<Annotation> all =
  META_PRESENT.all(element, Aliasing.spring());
```

---

## JUnit recipes

### Test extension discovery

JUnit's `@ExtendWith` is commonly buried inside composed annotations.
`@SpringBootTest`, for example, meta-annotates
`@ExtendWith(SpringExtension.class)`.

**JUnit 5 (`AnnotationSupport`):**

```java
// finds a single @ExtendWith, following composed annotations
Optional<ExtendWith> ext =
  AnnotationSupport.findAnnotation(testClass, ExtendWith.class);

// for multiple @ExtendWith (it is @Repeatable)
List<ExtendWith> exts =
  AnnotationSupport.findRepeatableAnnotations(testClass, ExtendWith.class);
```

**annogami:**

```java
// declared annotations on the class only
List<ExtendWith> exts =
  META_DIRECT_OR_INDIRECT.find(ExtendWith.class, MyTest.class);

// also considers extensions declared on a superclass
List<ExtendWith> exts =
  META_ASSOCIATED.find(ExtendWith.class, MyTest.class);
```

JUnit's `findRepeatableAnnotations` unwraps `@Repeatable` containers as part
of its contract. annogami walks the meta-annotation chain and returns every
instance it finds there, which has the same practical result for `@ExtendWith`
but via a different mechanism.

---

### Composed test annotations

```java

@Target(METHOD)
@Retention(RUNTIME)
@Test
@Tag("fast")
@Tag("unit")
public @interface FastUnitTest {
}
```

**JUnit 5 (`AnnotationSupport`):**

```java
// follows composed annotations for presence check
boolean isTest =
  AnnotationSupport.isAnnotated(method, Test.class);

// finds repeatable @Tag through composed annotations
List<Tag> tags =
  AnnotationSupport.findRepeatableAnnotations(method, Tag.class);
```

**annogami:**

```java
boolean isTest =
  !META_DIRECT_OR_INDIRECT.find(Test.class, method).isEmpty();

List<Tag> tags =
  META_DIRECT_OR_INDIRECT.find(Tag.class, method);
// → [Tag("fast"), Tag("unit")]
```

Both approaches handle composed annotations and repeatable `@Tag`. annogami
makes the meta-chain traversal explicit through the choice of presence
constant.

---

### `@BeforeEach` through the method override hierarchy

JUnit 5 discovers `@BeforeEach` on superclass methods automatically during test
execution, but its public API operates at the class level.

**JUnit 5 (`AnnotationSupport`):**

```java
// finds all @BeforeEach methods across the class hierarchy;
// no direct API to query a specific method's override chain
List<Method> setupMethods =
  AnnotationSupport.findAnnotatedMethods(
    testClass,
    BeforeEach.class,
    HierarchyTraversalMode.TOP_DOWN);
```

**annogami:**

```java
Method method = MyTest.class.getMethod("setUp");

AnnotatedPath path =
  AnnotatedPathBuilder.fromMethod(method)
    .toDepthOverridden()
    .build();

// present on the most-specific override, or any ancestor
Optional<BeforeEach> beforeEach =
  path.findFirst(BeforeEach.class, DIRECT);
```

JUnit's utility answers "which methods in this class have `@BeforeEach`?";
annogami's path answers "does this specific method, or any method it
overrides, have `@BeforeEach`?". They address complementary questions.

---

## Combining path and aliasing

The recipes above compose freely. For example, to find `@RequestMapping`
(synthesized from any composed mapping annotation) considering the full method
override hierarchy:

**Spring (no direct equivalent):**
Spring's utilities operate on a single element at a time; replicating this
requires manual iteration over the override chain.

**annogami:**

```java
Method method = MyController.class.getMethod("handleRequest");

AnnotatedPath path =
  AnnotatedPathBuilder
    .fromMethod(method)
    .toDepthOverridden()
    .build();

List<RequestMapping> mappings =
  path.find(
    RequestMapping.class,
    META_DIRECT_OR_INDIRECT,
    Aliasing.spring());
```

The first element in `mappings` is from the most-specific override; later
elements are from overridden ancestors. Use `path.findFirst` if only the most
specific mapping is needed.

Similarly, attribute-level merging across an override chain has no Spring
equivalent:

```java
// method-level @Transactional wins per attribute;
// superclass method fills in defaulted attributes
Optional<Transactional> tx =
  path.merge(Transactional.class, DIRECT, Aliasing.spring());
```
