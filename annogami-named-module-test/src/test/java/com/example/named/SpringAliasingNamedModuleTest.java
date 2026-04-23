package com.example.named;

import com.example.named.annotations.GetRoute;
import com.example.named.annotations.Route;
import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import static com.pholser.annogami.Presences.META_DIRECT;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasingNamedModuleTest {
  @GetRoute("/users")
  static class UserControl {}

  @Test
  void synthesizesRouteFromGetRouteOnNamedModuleAnnotationType() {
    Route r = META_DIRECT
      .find(Route.class, UserControl.class, SpringAliasing.spring())
      .orElseGet(Assertions::fail);
    assertThat(r.path()).isEqualTo("/users");
  }
}
