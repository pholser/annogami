package com.pholser.spring;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

class AliasValidationProcessorTest {
  private Compiler compiler() {
    return Compiler.javac().withProcessors(new AliasValidationProcessor());
  }

  @Test
  @DisplayName(
    "Happy path: intra-annotation alias with same type/default, OK usage")
  void validIntraAnnotationAlias_noConflicts() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.ValidIntra",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Meta {
            String value() default "meta";
          }

          @Meta @interface MyMapping {
            @AliasFor("path") String value() default "/default";
            @AliasFor("value") String path() default "/default";
          }

          class Usage {
            @MyMapping("/foo") void handler() {}
          }
          """);

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).succeeded();
  }

  @Test
  @DisplayName("Conflicting explicit values on two aliases in same annotation")
  void conflictingAliasValuesInUsage() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.ConflictingUsage",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Meta {
            String value() default "meta";
          }

          @Meta @interface MyMapping {
            @AliasFor("path") String value() default "/default";
            @AliasFor("value") String path() default "/default";
          }

          class Usage {
            @MyMapping(value = "/foo", path = "/bar") void handler() {}
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("are aliases and must have the same value")
      .inFile(source)
      .onLineContaining("@MyMapping(value = \"/foo\", path = \"/bar\")");
  }

  @Test
  @DisplayName(
    "@AliasFor on non-method element (e.g. on the annotation type itself)")
  void aliasForOnNonMethod() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.AliasForOnType",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @AliasFor @interface BadAnnotation {
            String value() default "";
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining(
        "@AliasFor can only be used on annotation attribute methods");
  }

  @Test
  @DisplayName("@AliasFor on method not enclosed in annotation type")
  void aliasForOnMethodNotInAnnotationType() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.AliasForOnRegularMethod",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          class NotAnAnnotation {
            @AliasFor public String value() {
              return "";
            }
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining(
        "@AliasFor used on a method not enclosed in an annotation type");
  }

  @Test
  @DisplayName("Target attribute name does not exist on target annotation")
  void aliasForMissingTargetAttribute() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.MissingTargetAttribute",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Meta {
            String value() default "meta";
          }

          @Meta @interface MyAnnotation {
            @AliasFor("doesNotExist") String value() default "x";
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining(
        "Target attribute 'doesNotExist' not found on annotation");
  }

  @Test
  @DisplayName("Mismatched return types between alias source and target")
  void mismatchedReturnTypes() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.MismatchedTypes",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Meta {
            String value() default "meta";
          }

          @Meta @interface MyAnnotation {
            @AliasFor("other") String value() default "x";
            @AliasFor("value") int other() default 1;
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining(
        "@AliasFor attributes must have the same return type");
  }

  @Test
  @DisplayName("Missing defaults on alias attributes")
  void missingDefaultsOnAliasAttributes() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.MissingDefaults",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Meta {
            String value() default "meta";
          }

          @Meta @interface MyAnnotation {
            @AliasFor("other") String value();
            @AliasFor("value") String other();
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("@AliasFor attributes must declare default values");
  }

  @Test
  @DisplayName("Mismatched defaults between alias attributes")
  void mismatchedDefaults() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.MismatchedDefaults",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Meta {
            String value() default "meta";
          }

          @Meta @interface MyAnnotation {
            @AliasFor("other") String value() default "a";
            @AliasFor("value") String other() default "b";
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("@AliasFor attributes must declare the same default value");
  }

  @Test
  @DisplayName(
    "annotation = ... but declaring annotation not meta-annotated with it")
  void metaAnnotationMissingWhenAnnotationAttributeUsed() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.MetaAnnotationMissing",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;
          import java.lang.annotation.Retention;
          import java.lang.annotation.RetentionPolicy;

          @Retention(RetentionPolicy.RUNTIME)
          @interface Meta {
            String value() default "meta";
          }

          @interface MyAnnotation {
            @AliasFor(annotation = Meta.class, attribute = "value")
            String value() default "x";
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("must be meta-annotated with @example.Meta");
  }

  @Test
  @DisplayName("Conflicting alias values on a field annotation usage")
  void conflictingAliasValuesOnFieldUsage() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.ConflictingField",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface MyAnno {
            @AliasFor("path") String value() default "/default";
            @AliasFor("value") String path() default "/default";
          }

          class Owner {
            @MyAnno(value = "/foo", path = "/bar")
            String myField = "";
          }
          """);

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("are aliases and must have the same value")
      .inFile(source)
      .onLineContaining("@MyAnno(value = \"/foo\", path = \"/bar\")");
  }

  @Test
  @DisplayName("Conflicting alias values on a constructor parameter annotation usage")
  void conflictingAliasValuesOnConstructorParamUsage() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.ConflictingCtorParam",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface MyAnno {
            @AliasFor("path") String value() default "/default";
            @AliasFor("value") String path() default "/default";
          }

          class Owner {
            final String route;

            Owner(@MyAnno(value = "/foo", path = "/bar") String route) {
              this.route = route;
            }
          }
          """);

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("are aliases and must have the same value")
      .inFile(source)
      .onLineContaining("@MyAnno(value = \"/foo\", path = \"/bar\")");
  }

  @Test
  @DisplayName("Cross-annotation alias: source default differing from target default is allowed")
  void crossAnnotationAlias_differentDefaultsAreAllowed() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.DifferentDefaultsCross",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Route {
            String path() default "";
          }

          @Route
          @interface GetMapping {
            @AliasFor(annotation = Route.class, attribute = "path")
            String value() default "/";
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).succeeded();
  }

  @Test
  @DisplayName("Cross-annotation alias: source attribute with no default is allowed")
  void crossAnnotationAlias_sourceWithNoDefaultIsAllowed() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.NoDefaultCross",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Route {
            String path() default "";
          }

          @Route
          @interface GetMapping {
            @AliasFor(annotation = Route.class, attribute = "path")
            String value();
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).succeeded();
  }

  @Test
  @DisplayName("Happy path: cross-annotation alias with implicit 'value' target attribute")
  void validCrossAnnotationAlias_implicitValueTarget() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.ImplicitValue",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface Route {
            String value() default "";
          }

          @Route
          @interface GetMapping {
            @AliasFor(annotation = Route.class)
            String value() default "";
          }

          class Usage {
            @GetMapping("/users") void handler() {}
          }
          """);

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).succeeded();
  }

  @Test
  @DisplayName("Happy path: cross-annotation alias where declaring annotation is meta-annotated")
  void validCrossAnnotationAlias_declaringAnnotationIsMetaAnnotated() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.ValidCross",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;
          import java.lang.annotation.Retention;
          import java.lang.annotation.RetentionPolicy;

          @Retention(RetentionPolicy.RUNTIME)
          @interface Route {
            String path() default "";
          }

          @Route
          @interface GetMapping {
            @AliasFor(annotation = Route.class, attribute = "path")
            String value() default "";
          }

          class Usage {
            @GetMapping("/users") void handler() {}
          }
          """);

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).succeeded();
  }

  @Test
  @DisplayName("Non-mirrored intra-alias: target attribute has no @AliasFor back")
  void nonMirroredIntraAlias_targetMissingAlias() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.NonMirrored",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface MyAnno {
            @AliasFor("path") String value() default "/default";
            String path() default "/default";
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("must be mirrored")
      .inFile(source)
      .onLineContaining("@AliasFor(\"path\") String value()");
  }

  @Test
  @DisplayName("Non-mirrored intra-alias: target aliases a different attribute, not back to source")
  void nonMirroredIntraAlias_targetAliasesDifferentAttribute() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.MisdirectedMirror",
        """
          package example;

          import org.springframework.core.annotation.AliasFor;

          @interface MyAnno {
            @AliasFor("b") String a() default "";
            @AliasFor("c") String b() default "";
            @AliasFor("b") String c() default "";
          }
          """
      );

    Compilation compilation = compiler().compile(source);

    assertThat(compilation).failed();
    assertThat(compilation)
      .hadErrorContaining("must be mirrored")
      .inFile(source)
      .onLineContaining("@AliasFor(\"b\") String a()");
  }

  @Test
  @DisplayName("Sanity check: processor is a no-op when AliasFor isn't present")
  void noAliasForOnClasspath_noWorkDone() {
    JavaFileObject source =
      JavaFileObjects.forSourceString(
        "example.NoAlias",
        """
          package example;

          @interface NoAlias {
            String value() default "";
          }

          class Usage {
            @NoAlias("x") void handler() {}
          }
          """
      );

    // Here we instantiate the processor directly; in a real world case,
    // aliasForElement would be null and process() just returns false.
    Compilation compilation = compiler().compile(source);

    assertThat(compilation).succeeded();
  }
}
