# Duly Noted: Annotation-finding strategies for Java

Duly Noted is a Java library that facilitates finding and manipulating
Java annotations on program elements at runtime.

Duly Noted wraps the basic annotation-finding facilities of the JDK and
offers similar functionality to Spring's annotation utilities. Its main
design elements are:

* *Presence levels* as a first-class concept. Rather than having
to remember which method of
[AnnotatedElement](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/AnnotatedElement.html)
corresponds to which presence level, or which of the Spring or JUnit
utility methods bake in certain levels of presence, instead you can use
methods like `find()`, `findAll()`, and `all()` from a clearly-named
implementation of a presence level on `Presences` to perform the operation
on the element.

* *Meta-presence*, as exploited in Spring and JUnit 5, as a first-class
concept. An annotation is *meta-present* on an element if it is
*presence-level* on the element, or *presence-level* on any annotations
that are *presence-level* on the element, and so on up the annotation
"hierarchy". A `MetaPresence` wraps another `Presence` to allow for
finding meta-present annotations on program elements.

* *Annotated path*: a sequence of program elements along which
Duly Noted looks for and merges annotations. Many Spring and JUnit 5
annotation helper methods prescribe a search path for the desired
annotations; if you want a separate path, find a separate method.
Duly Noted takes the approach of allowing for building a search path
for annotations, then calling methods on the path such as
`find(Class, SingleByTypeDetector)` and `all(AllDetector)` to perform the
desired operations. The presence level implementations from `Presence`
support these detector types.


## Capabilities to be added

* [x] Direct presence, direct-or-indirect presence, presence, associated
  * [x] On non-classes and classes
* [x] find-one by type, find-all by type, all: as appropriate for above
* [x] Meta-presence: either <presence-level> on an element, or
  recursively <presence-level> on one of the annotations that are
  <presence-level> on the element (TODO: need more tests)

* [x] Model `AnnotatedPath` as an abstraction over a sequence of
    `AnnotatedElements`
  * Along such a path, support the following operations:
    * [x] find first occurrence of a non-repeatable annotation
      along the path
    * [x] find every occurrence of a non-repeatable annotation
      along the path
    * [ ] corresponding merge operation along the path:
      give a synthesized annotation, with attributes at
      front of path superseding attributes further back
    * [x] find all occurrences of a repeatable annotation
      along the path
    * [ ] corresponding merge operation along the path:
      give synthesized annotation, with attributes at
      front of path superseding attributes further back
    * [ ] find all annotations along the path
    * [ ] corresponding merge operation along the path:
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
  * [ ] From method to methods it overrides
    * [ ] Depth-first or breadth-first thru superclass/interfaces
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

* empty class enclosure test
* empty class hierarchy test
* [x] Is it ok for these to be empty, and extended if they are
  (with the "then" items as above)?
  * Answer: yes; test for these

