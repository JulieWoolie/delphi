package net.arcadiusmc.chimera.selector;

import lombok.Getter;
import net.arcadiusmc.dom.Element;

@Getter
public class RegularSelector implements Selector {

  private final SelectorNode[] nodes;
  private final Spec spec;

  private String cachedToString;

  public RegularSelector(SelectorNode[] nodes) {
    this.nodes = nodes;

    this.spec = new Spec();
    for (SelectorNode node : nodes) {
      node.appendSpec(spec);
    }
  }

  @Override
  public boolean test(Element el) {
    if (nodes.length < 1) {
      return false;
    }

    int idx = nodes.length - 1;
    Combinator combinator = null;

    while (idx >= 0) {
      SelectorNode node = nodes[idx--];

      if (combinator != null) {
        el = combinator.findNextMatching(el, node);

        if (el == null) {
          return false;
        }
      } else if (!node.test(el)) {
        return false;
      }

      combinator = node.combinator;
    }

    return true;
//
//    if (!nodes[nodes.length - 1].test(root, el)) {
//      return false;
//    }
//
//    if (nodes.length < 2) {
//      return true;
//    }
//
//    int nodeIndex = nodes.length - 2;
//    Element next = el;
//
//    while (nodeIndex >= 0) {
//      SelectorNode node = nodes[nodeIndex--];
//      next = node.combinator.findNextMatching(root, next, node);
//
//      if (next == null) {
//        return false;
//      }
//    }
//
//    return true;
  }

  @Override
  public void append(StringBuilder builder) {
    for (int i = 0; i < nodes.length; i++) {
      SelectorNode node = nodes[i];
      node.append(builder, i != 0);
    }
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.add(this.spec);
  }

  @Override
  public String toString() {
    if (cachedToString != null) {
      return cachedToString;
    }

    StringBuilder builder = new StringBuilder();
    append(builder);

    return cachedToString = builder.toString();
  }
}
