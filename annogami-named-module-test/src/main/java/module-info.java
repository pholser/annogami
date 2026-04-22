// This module simulates a named-module consumer of annogami whose annotation
// package is only visible to the test module — NOT to the annogami modules
// themselves. This is a qualified export: it restricts compile-time import
// access and causes direct m.invoke(annotation) calls from
// com.pholser.annogami.* to throw IllegalAccessException, proving that annogami
// must dispatch through the proxy InvocationHandler rather than invoking methods
// directly.
module com.example.named {
    requires com.pholser.annogami.spring;
    requires com.pholser.annogami.programmatic;
    requires spring.core;

    // Qualified export: only the test module can access these types directly.
    // annogami modules are intentionally excluded.
    exports com.example.named.annotations to com.example.named.test;
}
