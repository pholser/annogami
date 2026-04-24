package com.pholser.annogami;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static com.pholser.annogami.Presences.META_DIRECT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The default meta-walk config prevents descent into
 * {@code java.lang.annotation.*} types. Observable effects through the public
 * API:
 * <ul>
 *   <li>Self-referential annotations like {@code @Documented} do not cause
 *       infinite traversal — the filter prevents re-entering the JDK type.</li>
 *   <li>Custom meta-annotation types separated by
 *       {@code java.lang.annotation.*} types in the chain are still reachable
 *       because the walker visits the CUSTOM nodes in the chain before reaching
 *       any JDK type.</li>
 *   <li>A java.lang.annotation.* type that is only reachable by descending
 *       THROUGH another java.lang.annotation.* node (not by being annotated on
 *       a custom node) won't appear in the walk results.</li>
 * </ul>
 */
class BreadthFirstMetaWalkerJavaLangAnnotationFilterTest {
  // --- Chain with @Documented (self-referential JDK type) terminates ---

  // @Documented is meta-annotated with @Documented itself. Without the filter,
  // descent into @Documented would recurse indefinitely. Verify the walk
  // terminates and still finds the custom meta-annotation.

  @Documented
  @Retention(RUNTIME)
  @interface Framework {
  }

  @Framework
  @Retention(RUNTIME)
  @interface Controller {
  }

  @Controller
  static class MyController {
  }

  @Test
  void metaWalkTerminatesEvenWithSelfReferentialDocumentedInChain() {
    // @Framework has @Documented on it. @Documented has @Documented on itself.
    // If descent into java.lang.annotation.* were not blocked, this would
    // recurse.

    assertThat(META_DIRECT.find(Framework.class, MyController.class))
      .isPresent();
  }

  @Test
  void customMetaAnnotationIsReachableThroughChainContainingJdkTypes() {
    assertThat(META_DIRECT.find(Controller.class, MyController.class))
      .isPresent();
  }

  // --- Type only reachable through a JDK node is not found ---

  // @Documented is meta-annotated with @Documented (a JDK type). If we look
  // for an annotation that is ONLY present on @Documented.class itself (i.e.,
  // reachable only by descending into @Documented), it won't be found because
  // the filter prevents that descent.
  //
  // @Retention is meta-annotated with @Documented. The only way to find
  // @Documented-on-Documented is to descend into @Documented, which is blocked.
  // Our custom @OnlyOnDocumented (if it existed on Documented.class) would
  // therefore not be found.
  //
  // The closest practical test: an annotation type whose only meta-annotation
  // is a java.lang.annotation.* type has no further custom meta-chain to find.

  @Retention(RUNTIME)
  @interface LeafMeta {
  }

  // @LeafMeta has only @Retention (a java.lang.annotation.* type) as its
  // meta-annotation. The filter blocks descent into Retention.class, so
  // nothing on Retention.class is reachable from here. Any type that is ONLY
  // on Retention.class itself is therefore not found through LeafMeta.

  @LeafMeta
  @Retention(RUNTIME)
  @interface Service {
  }

  @Service
  static class MyService {
  }

  @Test
  void findOfLeafMetaAnnotationSucceeds() {
    assertThat(META_DIRECT.find(LeafMeta.class, MyService.class)).isPresent();
  }

  @Test
  void findOfJdkTypeNotPresentOnAnyCustomNodeInChainReturnsEmpty() {
    // @Documented is not on any annotation in the MyService chain
    // (Service has only @LeafMeta and @Retention; LeafMeta has only @Retention).
    // Retention and its meta-annotations are blocked from descent.
    // So @Documented is not reachable here.
    assertThat(META_DIRECT.find(Documented.class, MyService.class)).isEmpty();
  }
}
