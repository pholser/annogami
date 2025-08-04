package com.pholser.annogami.annotated;

import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;

@Iota(-12)
public interface IAnnotationsGalore2 {
  @Atom(23) IAnnotationsGalore2 self(int i, Object o);
}
