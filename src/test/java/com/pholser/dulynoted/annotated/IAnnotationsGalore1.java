package com.pholser.dulynoted.annotated;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Iota;

@Iota(-14)
public interface IAnnotationsGalore1
  extends IAnnotationsGalore2, IAnnotationsGalore4 {

  @Atom(25) IAnnotationsGalore1 self(int i, Object o);
}
