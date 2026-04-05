package com.pholser.annogami.contract.allbytype;

import com.pholser.annogami.AllByType;

import static com.pholser.annogami.Presences.DIRECT_OR_INDIRECT;

final class DirectOrIndirectAllByTypeAliasingContractTest
  extends AllByTypeAliasingContractTest {

  @Override protected AllByType subject() {
    return DIRECT_OR_INDIRECT;
  }

  @Override protected boolean honorsInherited() {
    return false;
  }
}
