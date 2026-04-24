package com.example.named;

import com.example.named.annotations.GetRoute;
import com.example.named.annotations.Route;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.META_DIRECT;
import static com.pholser.annogami.spring.SpringAliasing.spring;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasingNamedModuleTest {
  @GetRoute("/users")
  static class UserControl {}

  @Test
  void synthesizesRouteFromGetRouteOnNamedModuleAnnotationType() {
    assertThat(
      META_DIRECT.find(Route.class, UserControl.class, spring()))
        .hasValueSatisfying(r ->
          assertThat(r.path()).isEqualTo("/users"));
  }
}
