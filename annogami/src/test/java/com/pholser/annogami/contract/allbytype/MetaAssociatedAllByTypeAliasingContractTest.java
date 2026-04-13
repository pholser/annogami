package com.pholser.annogami.contract.allbytype;

import com.pholser.annogami.AllByType;

import static com.pholser.annogami.Presences.META_ASSOCIATED;

final class MetaAssociatedAllByTypeAliasingContractTest
  extends AllByTypeAliasingContractTest {

  @Override
  protected AllByType subject() {
    return META_ASSOCIATED;
  }

  @Override
  protected boolean honorsInherited() {
    return true;
  }
}
