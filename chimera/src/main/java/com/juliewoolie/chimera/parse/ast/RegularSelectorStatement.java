package com.juliewoolie.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.parse.CompilerErrors;
import com.juliewoolie.chimera.selector.Combinator;
import com.juliewoolie.chimera.selector.RegularSelector;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.chimera.selector.SelectorNode;

@Getter @Setter
public class RegularSelectorStatement extends SelectorExpression {

  private final List<SelectorNodeStatement> nodes = new ArrayList<>();

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.selector(this);
  }

  public Selector compile(CompilerErrors errors) {
    if (nodes.isEmpty()) {
      return Selector.MATCH_ALL;
    }
    if (nodes.size() == 1) {
      SelectorNode node = nodes.getFirst().compile(errors);
      Selector selector = node.getSelector();

      if (node.getCombinator() == Combinator.DESCENDANT) {
        return selector;
      }

      return node;
    }

    SelectorNode[] nodes = new SelectorNode[this.nodes.size()];
    for (int i = 0; i < this.nodes.size(); i++) {
      nodes[i] = this.nodes.get(i).compile(errors);
    }
    return new RegularSelector(nodes);
  }
}
