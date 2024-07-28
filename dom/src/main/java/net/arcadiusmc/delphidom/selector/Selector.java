package net.arcadiusmc.delphidom.selector;

import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;
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

  public boolean test(DelphiElement el) {
    for (int i = nodes.length - 1; i >= 0; i--) {
      SelectorNode n = nodes[i];

      if (!n.test(el)) {
        return false;
      }
    }

    return true;
  }

  public DelphiElement selectOne(DelphiElement element) {
    List<DelphiElement> list = selectAll(element);
    return list.isEmpty() ? null : list.getFirst();
  }

  public List<DelphiElement> selectAll(DelphiElement element) {
    List<DelphiElement> out = new ArrayList<>();
    selectNodes(element, 0, out);
    return out;
  }

  private void selectNodes(DelphiNode node, int index, List<DelphiElement> out) {
    if (!(node instanceof DelphiElement el)) {
      return;
    }

    SelectorNode selectorNode = nodes[index];
    List<DelphiElement> selected = selectorNode.select(el);

    if (selected.isEmpty()) {
      return;
    }

    if (index < (nodes.length - 1)) {
      for (DelphiElement delphiElement : selected) {
        selectNodes(delphiElement, index + 1, out);
      }
    } else {
      out.addAll(selected);
    }
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
