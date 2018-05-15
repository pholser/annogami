package com.pholser.dulynoted;

import java.lang.reflect.AnnotatedElement;

class SearchPlanSegment {
    private final AnnotatedElement target;
    private final AllDetector presence;

    SearchPlanSegment(AnnotatedElement target, AllDetector presence) {
        this.target = target;
        this.presence = presence;
    }
}
