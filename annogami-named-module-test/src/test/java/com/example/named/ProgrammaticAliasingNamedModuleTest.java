package com.example.named;

import com.example.named.annotations.GetMapping;
import com.example.named.annotations.Route;
import com.pholser.annogami.programmatic.ProgrammaticAliasing;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProgrammaticAliasingNamedModuleTest {
  @GetMapping("/orders")
  static class OrderController {}

  @Test
  void synthesizesRouteFromGetMappingOnNamedModuleAnnotationType() {
    var aliasing = ProgrammaticAliasing.builder()
      .alias(GetMapping.class, "value", Route.class, "path")
      .build();

    GetMapping ann = OrderController.class.getAnnotation(GetMapping.class);

    assertThat(aliasing.synthesize(Route.class, List.of(ann)))
      .isPresent()
      .hasValueSatisfying(r -> assertThat(r.path()).isEqualTo("/orders"));
  }
}
