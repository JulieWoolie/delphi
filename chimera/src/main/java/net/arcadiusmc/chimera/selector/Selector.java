package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;
import org.jetbrains.annotations.NotNull;

public class Selector implements Comparable<Selector> {
  public static final Selector EMPTY = new Selector(new SelectorNode[0]);

  private final SelectorNode[] nodes;
  private final Spec spec;

  private String toStringCached;

  public Selector(SelectorNode[] nodes) {
    this.nodes = nodes;
    this.spec = new Spec();

    for (SelectorNode node : nodes) {
      node.appendSpec(spec);
    }
  }
  
  public boolean test(Element root, Element el) {
    if (nodes.length < 1) {
      return false;
    }

    if (!nodes[nodes.length - 1].test(root, el)) {
      return false;
    }

    if (nodes.length < 2) {
      return true;
    }

    int nodeIndex = nodes.length - 2;
    Element next = el;

    while (nodeIndex >= 0) {
      SelectorNode node = nodes[nodeIndex--];
      next = node.combinator.findNextMatching(root, next, node);

      if (next == null) {
        return false;
      }
    }

    return true;
  }

  @Override
  public String toString() {
    if (toStringCached != null) {
      return toStringCached;
    }

    StringBuilder builder = new StringBuilder();
    append(builder);

    return toStringCached = builder.toString();
  }

  public void append(StringBuilder builder) {
    for (int i = 0; i < nodes.length; i++) {
      SelectorNode node = nodes[i];
      node.append(builder, i != (nodes.length - 1));
    }
  }

  @Override
  public int compareTo(@NotNull Selector o) {
    return spec.compareTo(o.spec);
  }
}
