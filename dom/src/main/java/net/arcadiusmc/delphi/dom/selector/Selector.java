package net.arcadiusmc.delphi.dom.selector;

import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.delphi.dom.DelphiNode;

public class Selector {

  private final SelectorNode[] nodes;
  private final Spec spec;

  public Selector(SelectorNode[] nodes) {
    this.nodes = nodes;
    this.spec = new Spec();

    for (SelectorNode node : nodes) {
      node.appendSpec(spec);
    }
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

    if (!selectorNode.test(el)) {
      return;
    }

    if (index == (nodes.length - 1)) {
      out.add(el);
      return;
    }

    for (DelphiNode delphiNode : el.childList()) {
      selectNodes(delphiNode, index + 1, out);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    append(builder);
    return builder.toString();
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
}
