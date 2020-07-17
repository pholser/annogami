# Duly Noted: Annotation-finding strategies for Java

Duly Noted is 
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
