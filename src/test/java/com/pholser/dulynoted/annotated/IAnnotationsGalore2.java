package com.pholser.dulynoted.annotated;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;

@Iota(-12)
public interface IAnnotationsGalore2 {
  @Atom(23) IAnnotationsGalore2 self(int i, Object o);
}
