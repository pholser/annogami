package com.example.named;

import com.example.named.annotations.GetRoute;
import com.example.named.annotations.Route;
import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;

import static com.pholser.annogami.Presences.META_DIRECT;
import static org.assertj.core.api.Assertions.assertThat;

class SpringAliasingNamedModuleTest {
  @GetRoute("/users")
  static class UserController {}

  @Test
  void synthesizesRouteFromGetRouteOnNamedModuleAnnotationType() {
    assertThat(
      META_DIRECT.find(Route.class, UserController.class, SpringAliasing.aliasing()))
      .isPresent()
      .hasValueSatisfying(r -> assertThat(r.path()).isEqualTo("/users"));
  }
}
