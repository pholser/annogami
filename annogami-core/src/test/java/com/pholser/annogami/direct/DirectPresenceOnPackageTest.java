package com.pholser.annogami.direct;

import com.pholser.annogami.pkg.A;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.DIRECT;
import static org.assertj.core.api.Assertions.assertThat;

class DirectPresenceOnPackageTest {
  @Test
  void findsDirectlyPresent() {
    Package pkg =
      getClass().getClassLoader().getDefinedPackage(
        "com.pholser.annogami.pkg");

    assertThat(DIRECT.find(A.class, pkg))
      .hasValueSatisfying(a ->
        assertThat(a.value()).isEqualTo(42));
  }

  @Test
  void missesNotDeclared() {
    assertThat(DIRECT.find(A.class, getClass().getPackage())).isEmpty();
  }
}
