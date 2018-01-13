package com.pholser.dulynoted.annotations;

import java.lang.annotation.Annotation;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class Units {
    private Units() {
        throw new UnsupportedOperationException();
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
                return item instanceof Unit && ((Unit) item).value() == i;
            }

            @Override public void describeTo(Description description) {
                description.appendText("a Unit with value ").appendValue(i);
            }
        };
    }
}
