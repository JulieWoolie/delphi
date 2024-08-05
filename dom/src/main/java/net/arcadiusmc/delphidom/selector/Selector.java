package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.parser.Parser;
import net.arcadiusmc.delphidom.parser.ParserErrors;
import org.jetbrains.annotations.NotNull;

public class Selector implements Comparable<Selector> {

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

  public static Selector parse(String query) {
    StringBuffer buf = new StringBuffer(query);
    Parser parser = new Parser(buf);
    ParserErrors errors = parser.getErrors();

    Selector selector = parser.selector();

    errors.orThrow();

    return selector;
  }

  public boolean test(DelphiElement root, DelphiElement el) {
    if (nodes.length < 1) {
      return false;
    }

    if (!nodes[nodes.length - 1].test(root, el)) {
      return false;
    }

    if (nodes.length < 2) {
      return true;
    }

    int minDepth = root == null ? -1 : root.getDepth();
    int nodeIndex = nodes.length - 2;
    DelphiElement p = el.getParent();

    while (p != null) {
      if (p.getDepth() < minDepth) {
        return false;
      }

      SelectorNode node = nodes[nodeIndex--];

      if (!node.test(root, p)) {
        p = p.getParent();
        continue;
      }

      if (nodeIndex < 0) {
        return true;
      }

      p = p.getParent();
    }

    return false;
  }

  public String debugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<selector src=").append('"').append(this).append('"').append(">");

    for (SelectorNode node : nodes) {
      builder.append("\n  <node>");

      for (SelectorFunction function : node.functions) {
        builder.append("\n");
        function.appendDebug(builder);
      }

      builder.append("\n  </node>");
    }

    builder.append("\n</selector>");
    return builder.toString();
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

      if (i != 0) {
        builder.append(' ');
      }

      node.append(builder);
    }
  }

  @Override
  public int compareTo(@NotNull Selector o) {
    return spec.compareTo(o.spec);
  }
}
