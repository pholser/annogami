package com.pholser.dulynoted.annotations;

import java.lang.annotation.Annotation;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;

public final class AnnotationMatching {
    private AnnotationMatching() {
        throw new UnsupportedOperationException();
    }

    public static Matcher<Atom> atomOfValue(int i) {
        return new TypeSafeMatcher<Atom>() {
            @Override protected boolean matchesSafely(Atom item) {
                return item.value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("an Atom with value ").appendValue(i);
            }
        };
    }

    public static Matcher<Annotation> atomAnnotationOfValue(int i) {
        return new TypeSafeMatcher<Annotation>() {
            @Override protected boolean matchesSafely(Annotation item) {
                return item instanceof Atom && ((Atom) item).value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("an Atom with value ").appendValue(i);
            }
        };
    }

    public static Matcher<Iota> iotaOfValue(int i) {
        return new TypeSafeMatcher<Iota>() {
            @Override protected boolean matchesSafely(Iota item) {
                return item.value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("an Iota with value ").appendValue(i);
            }
        };
    }

    public static Matcher<Annotation> iotaAnnotationOfValue(int i) {
        return new TypeSafeMatcher<Annotation>() {
            @Override protected boolean matchesSafely(Annotation item) {
                return item instanceof Iota && ((Iota) item).value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("an Iota with value ").appendValue(i);
            }
        };
    }

    public static Matcher<Particle> particleOfValue(int i) {
        return new TypeSafeMatcher<Particle>() {
            @Override protected boolean matchesSafely(Particle item) {
                return item.value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("a Particle with value ").appendValue(i);
            }
        };
    }

    public static Matcher<Annotation> particleAnnotationOfValue(int i) {
        return new TypeSafeMatcher<Annotation>() {
            @Override protected boolean matchesSafely(Annotation item) {
                return item instanceof Particle
                    && ((Particle) item).value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("a Particle with value ")
                    .appendValue(i);
            }
        };
    }

    public static Matcher<Unit> unitOfValue(int i) {
        return new TypeSafeMatcher<Unit>() {
            @Override protected boolean matchesSafely(Unit item) {
                return item.value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("a Unit with value ").appendValue(i);
            }
        };
    }

    public static Matcher<Annotation> unitAnnotationOfValue(int i) {
        return new TypeSafeMatcher<Annotation>() {
            @Override protected boolean matchesSafely(Annotation item) {
                return item instanceof Unit
                    && ((Unit) item).value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("a Unit with value ")
                    .appendValue(i);
            }
        };
    }

    public static Matcher<Compound> compoundWith(
        Matcher<Particle>... particles) {

        return new TypeSafeMatcher<Compound>() {
            @Override protected boolean matchesSafely(Compound item) {
                return containsInAnyOrder(particles)
                    .matches(asList(item.value()));
            }

            @Override public void describeTo(Description description) {
                description.appendText("a Compound with values ")
                    .appendValue(asList(particles));
            }
        };
    }

    public static Matcher<Annotation> compoundAnnotationWith(
        Matcher<Particle>... particles) {

        return new TypeSafeMatcher<Annotation>() {
            @Override protected boolean matchesSafely(Annotation item) {
                return item instanceof Compound
                    && compoundWith(particles).matches(item);
            }

            @Override public void describeTo(Description description) {
                description.appendText("a Compound with values ")
                    .appendValue(asList(particles));
            }
        };
    }

    public static Matcher<Aggregate> aggregateWith(
        Matcher<Unit>... units) {

        return new TypeSafeMatcher<Aggregate>() {
            @Override protected boolean matchesSafely(Aggregate item) {
                return containsInAnyOrder(units)
                    .matches(asList(item.value()));
            }

            @Override public void describeTo(Description description) {
                description.appendText("an Aggregate with values ")
                    .appendValue(asList(units));
            }
        };
    }

    public static Matcher<Annotation> aggregateAnnotationWith(
        Matcher<Unit>... units) {

        return new TypeSafeMatcher<Annotation>() {
            @Override protected boolean matchesSafely(Annotation item) {
                return item instanceof Aggregate
                    && aggregateWith(units).matches(item);
            }

            @Override public void describeTo(Description description) {
                description.appendText("an Aggregate with values ")
                    .appendValue(asList(units));
            }
        };
    }
}
