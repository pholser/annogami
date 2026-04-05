package com.pholser.annogami.contract.all;

import com.pholser.annogami.All;

import static com.pholser.annogami.Presences.PRESENT;

final class PresentAllAliasingContractTest extends AllAliasingContractTest {
  @Override protected All subject() {
    return PRESENT;
  }

  @Override protected boolean honorsInherited() {
    return true;
  }
}
