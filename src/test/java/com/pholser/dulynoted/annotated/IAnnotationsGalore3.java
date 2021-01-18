package com.pholser.dulynoted.annotated;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;

@Iota(-16)
public interface IAnnotationsGalore3 extends IAnnotationsGalore6 {
  @Atom(27) IAnnotationsGalore6 self(int i, Object o);
}
