module com.example.named.test {
    requires com.example.named;

    // com.example.named does not re-export these transitively,
    // so the test module must require them directly.
    requires com.pholser.annogami.spring;
    requires com.pholser.annogami.programmatic;

    requires org.junit.jupiter.api;
    requires org.assertj.core;

    // JUnit Platform Commons needs reflective access to discover and run tests.
    opens com.example.named to org.junit.platform.commons;
}
