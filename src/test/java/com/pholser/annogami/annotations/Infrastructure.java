package com.pholser.annogami.annotations;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Test infrastructure containing all test annotations, test classes, and
 * shared test data used across the test suite.
 * This mirrors the pattern used in Spring's AnnotationUtilsTests and
 * JUnit's AnnotationSupportTests where test infrastructure is centralized.
 */
public final class Infrastructure {
  private Infrastructure() {
    throw new AssertionError();
  }

  @Retention(RUNTIME)
  @Target({TYPE, METHOD, FIELD, PARAMETER})
  public @interface DirectAnnotation {
    String value() default "direct";

    int priority() default 0;
  }

  @Retention(RUNTIME)
  @Target({TYPE, METHOD, ANNOTATION_TYPE})
  public @interface MetaAnnotation {
    String metaValue() default "meta";

    boolean enabled() default true;
  }

  @Retention(RUNTIME)
  @Target({TYPE, METHOD})
  @MetaAnnotation(metaValue = "composed-meta")
  public @interface ComposedAnnotation {
    String composedValue() default "composed";

    String name() default "composed-name";
  }

  @Retention(RUNTIME)
  @Target({METHOD, TYPE})
  public @interface RepeatableContainer {
    RepeatableAnnotation[] value();
  }

  @Retention(RUNTIME)
  @Target({METHOD, TYPE})
  @Repeatable(RepeatableContainer.class)
  public @interface RepeatableAnnotation {
    String value();

    int order() default 0;
  }

  @Retention(RUNTIME)
  @Target(METHOD)
  public @interface MethodAnnotation {
    String value() default "method";

    String[] tags() default {};
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  @Inherited
  public @interface ClassAnnotation {
    String value() default "class";

    boolean inherited() default true;
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  public @interface NonInheritedClassAnnotation {
    String value() default "non-inherited";
  }

  @Retention(RUNTIME)
  @Target(PARAMETER)
  public @interface ParameterAnnotation {
    String value() default "param";
  }

  @Retention(RUNTIME)
  @Target(FIELD)
  public @interface FieldAnnotation {
    String value() default "field";

    boolean required() default false;
  }

  @Retention(RUNTIME)
  @Target({TYPE, METHOD, PARAMETER})
  public @interface ConfigAnnotation {
    String name() default "default-name";

    String value() default "default-value";

    int priority() default 0;

    boolean enabled() default true;

    String[] profiles() default {};
  }

  @Retention(RUNTIME)
  @Target({TYPE, METHOD})
  @ConfigAnnotation(name = "meta-config", priority = 10, enabled = false)
  public @interface MetaConfigAnnotation {
    String name() default "meta-default";

    String value() default "meta-value";

    int priority() default 5;

    String[] profiles() default {"meta"};
  }

  @Retention(RUNTIME)
  @Target({TYPE, METHOD})
  public @interface AliasedAnnotation {
    String value() default "";

    String name() default "";
    // In a real implementation, these would be aliased
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  public @interface DefaultValueAnnotation {
    String explicit() default "default-explicit";

    String implicit(); // No default - must be specified
  }

  @ClassAnnotation("root")
  @ConfigAnnotation(name = "root", priority = 1, profiles = {"root", "base"})
  public static class RootClass {
    @FieldAnnotation("root-field")
    protected String rootField;

    @MethodAnnotation("root-method")
    @ConfigAnnotation(name = "root-method", value = "root-method-value")
    public void rootMethod(@ParameterAnnotation("root-param") String param) {
    }

    @DirectAnnotation("root-direct")
    public void directMethod() {
    }

    public void unAnnotatedMethod() {
    }
  }

  @ClassAnnotation("parent")
  @NonInheritedClassAnnotation("parent-non-inherited")
  @ConfigAnnotation(name = "parent", value = "parent-value", priority = 2)
  public static class ParentClass extends RootClass {
    @FieldAnnotation(value = "parent-field", required = true)
    protected String parentField;

    @Override
    @MethodAnnotation(value = "parent-method", tags = {"override", "parent"})
    @ConfigAnnotation(name = "parent-override", enabled = false)
    public void rootMethod(@ParameterAnnotation("parent-param") String param) {
    }

    @ComposedAnnotation(composedValue = "parent-composed")
    public void composedMethod() {
    }

    @RepeatableAnnotation("first")
    @RepeatableAnnotation("second")
    public void repeatableMethod() {
    }
  }

  @ClassAnnotation("child")
  @ComposedAnnotation(name = "child-composed")
  @ConfigAnnotation(name = "child", value = "child-value", priority = 3, profiles = {"child", "test"})
  public static class ChildClass extends ParentClass {
    @FieldAnnotation("child-field")
    private String childField;

    @Override
    @MethodAnnotation(value = "child-method", tags = {"child", "final"})
    @MetaConfigAnnotation(value = "child-meta", profiles = {"child", "meta"})
    public void rootMethod(@ParameterAnnotation("child-param") String param) {
    }

    @Override
    @ComposedAnnotation(composedValue = "child-composed-override")
    public void composedMethod() {
    }

    @RepeatableAnnotation(value = "child-first", order = 1)
    @RepeatableAnnotation(value = "child-second", order = 2)
    @RepeatableAnnotation(value = "child-third", order = 3)
    public void multipleRepeatableMethod() {
    }

    @DirectAnnotation(value = "child-only", priority = 99)
    public void childOnlyMethod() {
    }
  }

  @ConfigAnnotation(name = "grandchild", enabled = false, profiles = {"grandchild"})
  @DefaultValueAnnotation(implicit = "grandchild-implicit")
  public static class GrandChildClass extends ChildClass {
    @Override
    public void rootMethod(@ParameterAnnotation("grandchild-param") String param) {
      // No method-level annotations - tests inherited behavior
    }

    @DirectAnnotation("grandchild-direct")
    public void grandchildSpecificMethod() {
    }
  }

  @ClassAnnotation("base-interface")
  @ConfigAnnotation(name = "base-interface", priority = 100)
  public interface BaseInterface {
    @MethodAnnotation("interface-method")
    @ConfigAnnotation(name = "interface-method", value = "interface-value")
    default void interfaceMethod() {
    }

    @DirectAnnotation("interface-direct")
    void abstractMethod();
  }

  @ClassAnnotation("extended-interface")
  public interface ExtendedInterface extends BaseInterface {
    @Override
    @MethodAnnotation(value = "extended-interface-method", tags = {"extended"})
    default void interfaceMethod() {
    }

    @ComposedAnnotation
    void extendedMethod();
  }

  @ClassAnnotation("implementing-class")
  public static class ImplementingClass extends ChildClass implements ExtendedInterface {
    @Override
    @DirectAnnotation("implementing-abstract")
    public void abstractMethod() {
    }

    @Override
    @MetaConfigAnnotation(name = "implementing-extended")
    public void extendedMethod() {
    }
  }

  public static class PlainClass {
    public String plainField;

    public void plainMethod(String param) {
    }
  }

  @Deprecated // RetentionPolicy.SOURCE
  public static class SourceAnnotatedClass {
    @SuppressWarnings("unused") // RetentionPolicy.SOURCE
    public void sourceAnnotatedMethod() {
    }
  }

  public static class SingleRepeatableClass {
    @RepeatableAnnotation("single")
    public void singleRepeatableMethod() {
    }
  }

  public static final String ROOT_CLASS_ANNOTATION_VALUE = "root";
  public static final String PARENT_CLASS_ANNOTATION_VALUE = "parent";
  public static final String CHILD_CLASS_ANNOTATION_VALUE = "child";

  public static final String ROOT_METHOD_ANNOTATION_VALUE = "root-method";
  public static final String PARENT_METHOD_ANNOTATION_VALUE = "parent-method";
  public static final String CHILD_METHOD_ANNOTATION_VALUE = "child-method";

  public static final String[] EXPECTED_CHILD_PROFILES = {"child", "test"};
  public static final String[] EXPECTED_META_PROFILES = {"child", "meta"};

  public static final int ROOT_PRIORITY = 1;
  public static final int PARENT_PRIORITY = 2;
  public static final int CHILD_PRIORITY = 3;
  public static final int META_CONFIG_PRIORITY = 5; // From MetaConfigAnnotation default

  public static Method getMethod(Class<?> k, String name, Class<?>... paramTypes)
    throws Exception {

    return k.getMethod(name, paramTypes);
  }

  public static Field getField(Class<?> k, String name) throws Exception {
    return k.getDeclaredField(name);
  }

  public static Parameter getParameter(Method m, int index) {
    return m.getParameters()[index];
  }

  public static Object createInstance(Class<?> k) throws Exception {
    return k.getDeclaredConstructor().newInstance();
  }
}
