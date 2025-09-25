# Annogami: Annotation-finding strategies for Java

Annogami is a Java library that facilitates finding Java annotations on program
elements at runtime. It wraps the basic annotation-finding capabilities of the
JDK and offers similar functionality to
[Spring](https://spring.io/projects/spring-framework)'s and
[JUnit](https://junit.org/)'s annotation utilities.

## Design elements

* *Presence levels* as a first-class concept. Rather than having to remember
which method of
[AnnotatedElement](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/AnnotatedElement.html)
corresponds to which presence level, or which of the Spring or JUnit utility
methods bake in certain levels of presence, instead you can use methods
`find` and `all` from a clearly-named implementation of a presence level on
class `Presences` to perform the operation on the element.

* *Meta-presence*, as exploited in Spring and JUnit, as a first-class concept.
An annotation is *meta-present* on an element if it is *presence-level* on the
element, or *presence-level* on any annotations that are *presence-level* on
the element, and so on up the annotation "hierarchy". Meta-presence encourages
creation of composed annotations. There are meta-presences on class
`Presences` along with the usual presence levels.

* *Annotated path*: a sequence of program elements along which Annogami looks
for and merges annotations. Many Spring and JUnit utility methods prescribe a
search path for the desired annotations; if you want a separate path, find a
separate method. Annogami takes the approach of allowing for building a search
path for annotations, then calling methods `find` or `all` on the path using
a presence level implementation from `Presences` to perform the desired
operations.

## How-To

(*Note*: If only a single instance of a repeatable annotation of type `A`
is declared on a program element, it is directly present on the element.
If more than one such instance is declared on the element, those instances
become indirectly present on the element, and an instance of its container
element is directly present on the element.)

* Find a single annotation of type `A` *directly present* on a program
`element`:

```java
    Optional<A> a = Presences.DIRECT.find(A.class, element);
```

* Find all the annotations *directly present* on a program `element`:

```java
    List<Annotation> all = Presences.DIRECT.all(element);
```

* Find all the annotations of type `A` *directly or indirectly present*
on a program `element`:

```java
    List<A> as = Presences.DIRECT_OR_INDIRECT.find(A.class, element);
```

* Find a single annotation of type `A` *present* on a program
`element`:

```java
    Optional<A> a = Presences.PRESENT.find(A.class, element);
```

* Find all the annotations *present* on a program `element`:

```java
    List<Annotation> all = Presences.PRESENT.all(element);
```

* Find all the annotations of type `A` *associated* on a program `element`:

```java
    List<A> as = Presences.ASSOCIATED.find(A.class, element);
```

* Find the first annotation of type `A` either *directly present*
on a program `element`, or declared recursively on any of the annotations
that are *directly present* on `element:

```java
    Optional<A> a = Presences.META_DIRECT.find(A.class, element);
```

* Find all the annotations either *directly present* on a program
`element`, or declared recursively on any of the annotations that are
on a program `element`, or declared recursively on any of the annotations
that are *directly present* on `element`:

```java
    List<Annotation> all = Presences.META_DIRECT.all(element);
```

* Find all the annotations of type `A` *directly or indirectly present*
on a program `element`, or declared recursively on any of the annotations
that are *directly or indirectly present* on `element`:

```java
    List<A> as = Presences.META_DIRECT_OR_INDIRECT.find(A.class, element);
```

* Find a single annotation of type `A` *present* on a program
`element`, or declared recursively on any of the annotations that are
*present* on `element`:

```java
    Optional<A> a = Presences.META_PRESENT.find(A.class, element);
```

* Find all the annotations *present* on a program `element`, or declared
recursively on any of the annotations that are *present* on `element`:

```java
    List<Annotation> all = Presences.META_PRESENT.all(element);
```

* Find all the annotations of type `A` *associated* on a program `element`,
or declared recursively on any of the annotations that are *associated*
on `element`:

```java
    List<A> as = Presences.META_ASSOCIATED.find(A.class, element);
```

* Give an annotation of type `A` *meta-present* along a path of program
elements `path` starting at a method parameter `p`, where attribute values
of `A` found earlier in the path supersede attribute values of `A` found
later (an explicit non-default value for an attribute always supersedes
a default value):

```java
    AnnotatedPath path =
      AnnotatedPathBuilder.fromParameter(p)
        .toDeclaringMethod()
        .toDeclaringClass()
        .toDepthHierarchy()
        .build();
    A merged = path.merge(A.class, Presences.META_DIRECT);
```

## Capabilities to be added

* [x] Direct presence, direct-or-indirect presence, presence, associated
  * [x] On non-classes and classes
* [x] find-one by type, find-all by type, all: as appropriate for above
* [x] Meta-presence: either *<presence-level>* on an element, or
  recursively *<presence-level>* on one of the annotations that are
  *<presence-level>* on the element (TODO: need more tests)

* [x] Model `AnnotatedPath` as an abstraction over a sequence of
    `AnnotatedElements`
  * Along such a path, support the following operations:
    * [x] find first occurrence of a non-repeatable annotation
      along the path
    * [x] find every occurrence of a non-repeatable annotation
      along the path
    * [x] corresponding merge operation along the path:
      give a synthesized annotation, with attributes at
      front of path superseding attributes further back
    * [x] find all occurrences of a repeatable annotation
      along the path
    * [x] find all annotations along the path
    * [x] corresponding merge operation along the path:
      give synthesized annotations, with attributes at
      front of path superseding attributes further back
    * [ ] Figure out a way to handle merging of repeatable
      annotations, if we even want to do that.

* [ ] Implement several ways of producing meaningful
  `AnnotatedPath`s, outward in program element hierarchy
  * [x] From method parameter, from field, from method,
    from class, from package, from module
  * [x] From method parameter to declaring method
  * [x] From method to declaring class
  * [x] From class to package
  * [x] From class to module
  * [x] From field to declaring class
  * [x] From class to class enclosure
  * [x] From class to superclass/interface hierarchy
    * [x] Depth-first superclasses, then depth-first interfaces
    * [x] Breadth-first: superclass, then interfaces, ...
  * [x] From method to methods it overrides
    * [x] Depth-first or breadth-first thru superclass/interfaces
  * [ ] For path-building stages that yield multiples (classes,
    methods), offer a way to go "then" to next elements for each
    (e.g. all these methods, *then* all their declaring classes),
    or "depth-first" (from each of those methods, interweave calls
    out to declaring classes, then chain from each of those etc.
    so that method1/class1/...1 come before method2/class2/...2)

* [x] Would it be useful to be able to create `AnnotatedPath`s
  in the other direction: e.g. class to declared fields? Maybe
  distinguish between inward and outward `AnnotatedPath`s?
  * Answer: I'm going with "no".

* [x] empty class enclosure test
* [x] empty class hierarchy test
* [x] empty method override test
* [ ] Is it ok for these to be empty, and extended if they are
  (with the "then" items as above)?
  * Answer: yes; test for these
* [ ] empty class enclosure test with `then`
* [ ] empty class hierarchy test with `then`
* [ ] empty method override test with `then`
* [x] JPMS-modularize
