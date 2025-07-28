package com.juliewoolie.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.parse.CompilerErrors;
import com.juliewoolie.chimera.selector.Combinator;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.chimera.selector.SelectorList;
import com.juliewoolie.chimera.selector.SelectorList.ListStyle;
import com.juliewoolie.chimera.selector.SelectorList.ListType;
import com.juliewoolie.chimera.selector.SelectorNode;

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
