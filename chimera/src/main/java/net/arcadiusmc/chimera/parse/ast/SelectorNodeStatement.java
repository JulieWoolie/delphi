package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.selector.Combinator;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorList;
import net.arcadiusmc.chimera.selector.SelectorList.ListStyle;
import net.arcadiusmc.chimera.selector.SelectorList.ListType;
import net.arcadiusmc.chimera.selector.SelectorNode;

@Getter @Setter
public class SelectorNodeStatement extends SelectorExpression {

  private final List<SelectorExpression> expressions = new ArrayList<>();
  private Combinator combinator;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.selectorNode(this);
  }

  public SelectorNode compile(CompilerErrors errors) {
    SelectorNode node = new SelectorNode();
    node.setCombinator(combinator);

    if (expressions.isEmpty()) {
      return node;
    }
    if (expressions.size() == 1) {
      Selector selector = expressions.getFirst().compile(errors);

      if (selector != null) {
        node.setSelector(selector);
      }

      return node;
    }

    SelectorList list = new SelectorList(expressions.size());
    list.setStyle(ListStyle.COMPACT);
    list.setType(ListType.AND);

    node.setSelector(list);

    for (int i = 0; i < expressions.size(); i++) {
      Selector func = expressions.get(i).compile(errors);

      if (func == null) {
        continue;
      }

      list.add(func);
    }

    return node;
  }
}
