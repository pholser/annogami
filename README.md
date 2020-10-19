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
`find(SingleByTypeDetector)` and `all(AllDetector)` to perform the
desired operations. The presence level implementations from `Presence`
support these detector types.


## Capabilities to be added

* [x] Direct presence, direct-or-indirect presence, presence, associated
  * [x] On non-classes and classes
* [x] find-one by type, find-all by type, all: as appropriate for above
* [ ] Meta-presence: either <presence-level> on an element, or
  recursively <presence-level> on one of the annotations that are
  <presence-level> on the element

* [ ] Model `AnnotatedPath` as an abstraction over a sequence of
    `AnnotatedElements`
  * Along such a path, support the following operations:
    * [ ] find first occurrence of a non-repeatable annotation
      along the path
    * [ ] find every occurrence of a non-repeatable annotation
      along the path
    * [ ] corresponding merge operation along the path:
      give a synthesized annotation, with attributes at
      front of path superseding attributes further back
    * [ ] find all occurrences of a repeatable annotation
      along the path
    * [ ] corresponding merge operation along the path:
      give synthesized annotation, with attributes at
      front of path superseding attributes further back
    * [ ] find all annotations along the path
    * [ ] corresponding merge operation along the path:
      give synthesized annotations, with attributes at
      front of path superseding attributes further back

* Implement several ways of producing meaningful
  `AnnotatedPaths`
  * [ ] For example, starting with a method parameter,
    then its method, then method's declaring class, then
    superclass and interfaces and so forth, breadth-first
  * [ ] Method, then any method decls it overrides in
    superclass and interfaces, breadth-first
  * [ ] ...
