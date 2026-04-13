package com.pholser.annogami.contract.allbytype;

import com.pholser.annogami.AllByType;

import static com.pholser.annogami.Presences.ASSOCIATED;

final class AssociatedAllByTypeAliasingContractTest
  extends AllByTypeAliasingContractTest {

  @Override
  protected AllByType subject() {
    return ASSOCIATED;
  }

  @Override
  protected boolean honorsInherited() {
    return true;
  }
}
