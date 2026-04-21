// This module simulates a named-module consumer of annogami.
// It exports its annotation package so the types are publicly accessible,
// but deliberately does NOT open it — no "opens com.example.named.annotations".
// Without the proxy-dispatch fix in annogami, calls like m.invoke(annotation)
// from com.pholser.annogami.* into this module's annotation types will fail
// with IllegalAccessException because there is no read edge in that direction.
module com.example.named {
    requires com.pholser.annogami.spring;
    requires com.pholser.annogami.programmatic;
    requires spring.core;

    exports com.example.named.annotations;
}
