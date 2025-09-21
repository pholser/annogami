package com.pholser.annogami;

/**
 * Access point for the various implementations of "presence levels"
 * of Java annotations.
 */
public final class Presences {
  /**
   * Direct presence, to accommodate single instances of annotations.
   */
  public static final Direct DIRECT = new Direct();

  /**
   * Direct-or-indirect presence, to accommodate
   * {@linkplain java.lang.annotation.Repeatable repeatable} annotations.
   */
  public static final DirectOrIndirect DIRECT_OR_INDIRECT =
    new DirectOrIndirect();

  /**
   * Presence, to accommodate single instances of annotations, possibly
   * {@linkplain java.lang.annotation.Inherited inherited}.
   */
  public static final Present PRESENT = new Present();

  /**
   * Associated presence, to accommodate
   * {@linkplain java.lang.annotation.Repeatable repeatable} annotations,
   * possibly {@linkplain java.lang.annotation.Inherited inherited}.
   */
  public static final Associated ASSOCIATED = new Associated();

  /**
   * The "meta" version of {@link Direct}.
   */
  public static final MetaDirect META_DIRECT = new MetaDirect();

  /**
   * The "meta" version of {@link DirectOrIndirect}.
   */
  public static final MetaDirectOrIndirect META_DIRECT_OR_INDIRECT =
    new MetaDirectOrIndirect();

  /**
   * The "meta" version of {@link Present}.
   */
  public static final Meta META_PRESENT = new Meta();

  /**
   * The "meta" version of {@link Associated}.
   */
  public static final MetaAssociated META_ASSOCIATED =
    new MetaAssociated();

  private Presences() {
    throw new AssertionError();
  }
}
