package com.example.named;

import com.example.named.annotations.GetRoute;
import com.example.named.annotations.Route;
import com.pholser.annogami.AnnotatedPath;
import com.pholser.annogami.spring.SpringAliasing;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pholser.annogami.Presences.META_DIRECT;
import static org.assertj.core.api.Assertions.assertThat;

class MergeAliasingNamedModuleTest {
  @GetRoute("/users")
  static class UserController {
  }

  @Test
  void mergeWithAliasingWorksFromNamedModule() {
    AnnotatedPath path = new AnnotatedPath(List.of(UserController.class));

    assertThat(path.merge(Route.class, META_DIRECT, SpringAliasing.spring()))
      
      .hasValueSatisfying(r ->
        assertThat(r.path()).isEqualTo("/users"));
  }
}
