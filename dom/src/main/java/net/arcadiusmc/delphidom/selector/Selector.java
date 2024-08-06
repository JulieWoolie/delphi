package net.arcadiusmc.delphidom.selector;

import lombok.Getter;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.parser.Parser;
import net.arcadiusmc.delphidom.parser.ParserErrors;
import org.jetbrains.annotations.NotNull;

@Getter
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

    int nodeIndex = nodes.length - 2;
    DelphiElement next = el;

    while (nodeIndex >= 0) {
      SelectorNode node = nodes[nodeIndex--];
      next = node.combinator.findNextMatching(root, next, node);

      if (next == null) {
        return false;
      }
    }

    return true;
  }

  public String debugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<selector src=").append('"').append(this).append('"').append(">");

    for (SelectorNode node : nodes) {
      builder.append("\n  <node combinator=")
          .append('"')
          .append(node.getCombinator().name().toLowerCase())
          .append('"')
          .append('>');

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
      node.append(builder, i != (nodes.length - 1));
    }
  }

  @Override
  public int compareTo(@NotNull Selector o) {
    return spec.compareTo(o.spec);
  }
}
