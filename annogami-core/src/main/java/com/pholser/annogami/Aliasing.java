package com.pholser.annogami;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public interface Aliasing {
  <A extends Annotation> Optional<A> synthesize(
    Class<A> annoType,
    List<Annotation> metaContext);
}
