# Duly Noted: Annotation-finding strategies for Java

* Search path: a sequence of annotated elements
* Presence: a particular kind of annotation presence
  * Direct presence, via `getDeclaredAnnotation(Class<T>)`
  * Indirect presence
    * where the annotation is repeatable, and either the repeatable
    annotation is directly present or contained in a directly present
    container annotation



- [ ] Search path: an 
- [ ] Find the first annotation of a given type in a 
- [ ] Find annotation directly on a type declaration (class)
- [ ] Find annotation directly on a method
- [ ] Find annotation directly on a constructor
- [ ] Find annotation directly on a field
- [ ] Find annotation directly on a method/ctor parameter
- [ ] Find annotation directly on an annotation type
- [ ] Find annotation directly on a package declaration
- [ ] Find annotation directly on various type parameters
- [ ] Find annotation directly on various type use contexts
- [ ] (JDK 9: Find annotation directly on module declaration)

- [ ] Find annotation directly meta-present on an annotated element

- [ ] Class nesting
- [ ] Class hierarchy, depth-first, no repeats

- [ ] Find annotation directly meta-present on an annotated element
    or any enclosing class/package

...


- [ ] Include local and anonymous class possibilities for enclosing
    structures to find annotations on
