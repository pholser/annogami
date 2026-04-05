package com.pholser.annogami.contract.all;

import com.pholser.annogami.All;

import static com.pholser.annogami.Presences.DIRECT;

final class DirectAllAliasingContractTest extends AllAliasingContractTest {
  @Override protected All subject() {
    return DIRECT;
  }

  @Override protected boolean honorsInherited() {
    return false;
  }
}
