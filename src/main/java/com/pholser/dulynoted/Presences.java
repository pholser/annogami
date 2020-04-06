package com.pholser.dulynoted;

public final class Presences {
  public static final DirectPresence DIRECT = new DirectPresence();
  public static final DirectOrIndirectPresence DIRECT_OR_INDIRECT =
    new DirectOrIndirectPresence();
  public static final Presence PRESENT = new Presence();
  public static final AssociatedPresence ASSOCIATED =
    new AssociatedPresence();

  private Presences() {
    throw new UnsupportedOperationException();
  }
}
