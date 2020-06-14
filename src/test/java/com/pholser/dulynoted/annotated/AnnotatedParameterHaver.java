package com.pholser.dulynoted.annotated;

import com.pholser.dulynoted.annotations.Atom;
import com.pholser.dulynoted.annotations.Unit;

public final class AnnotatedParameterHaver {
  public AnnotatedParameterHaver(@Unit(2) int i) {
  }

  public void x(@Atom(1) int i) {
  }
}
