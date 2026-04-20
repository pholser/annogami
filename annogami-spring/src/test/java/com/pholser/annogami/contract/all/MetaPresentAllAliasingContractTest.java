package com.pholser.annogami.contract.all;

import com.pholser.annogami.All;

import static com.pholser.annogami.Presences.META_PRESENT;

final class MetaPresentAllAliasingContractTest extends AllAliasingContractTest {
  @Override
  protected All subject() {
    return META_PRESENT;
  }

  @Override
  protected boolean honorsInherited() {
    return true;
  }
}
