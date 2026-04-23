package com.pholser.annogami;

import java.util.HashMap;
import java.util.Map;

/**
 * A string-keyed union-find (disjoint-set) structure for grouping alias
 * equivalence classes.
 *
 * <p>Elements are strings introduced lazily on first access. Within each
 * group the canonical representative is always the lexicographically smallest
 * member, so two united elements always return the same {@link #find} result
 * regardless of the order in which {@link #union} was called.
 *
 * <p>Path compression is applied during {@link #find}, keeping subsequent
 * lookups near O(1).
 */
public final class UnionFind {
  private final Map<String, String> parent = new HashMap<>();

  /**
   * Returns the canonical representative of the group containing {@code s},
   * registering {@code s} as a singleton group if it has not been seen before.
   *
   * @param s the element to look up
   * @return the lexicographically smallest member of {@code s}'s group
   */
  public String find(String s) {
    parent.putIfAbsent(s, s);

    String p = parent.get(s);
    if (p.equals(s)) {
      return s;
    }

    String r = find(p);
    parent.put(s, r);
    return r;
  }

  /**
   * Merges the groups containing {@code a} and {@code b}.
   * If they are already in the same group this is a no-op. Otherwise, the
   * lexicographically larger root is re-parented under the smaller one,
   * preserving the invariant that {@link #find} always returns the smallest
   * member.
   *
   * @param a one element
   * @param b another element
   */
  public void union(String a, String b) {
    String groupA = find(a);
    String groupB = find(b);

    if (!groupA.equals(groupB)) {
      if (groupA.compareTo(groupB) < 0) {
        parent.put(groupB, groupA);
      } else {
        parent.put(groupA, groupB);
      }
    }
  }
}
