package com.pholser.annogami.annotated;

import com.pholser.annogami.annotations.Atom;
import com.pholser.annogami.annotations.Iota;

@Iota(-16)
public interface IAnnotationsGalore3 extends IAnnotationsGalore6 {
  @Atom(27) IAnnotationsGalore6 self(int i, Object o);
}
