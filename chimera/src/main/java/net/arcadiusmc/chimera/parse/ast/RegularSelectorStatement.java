package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.selector.Combinator;
import net.arcadiusmc.chimera.selector.RegularSelector;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorNode;

@Getter @Setter
public class RegularSelectorStatement extends SelectorExpression {

  private final List<SelectorNodeStatement> nodes = new ArrayList<>();

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.selector(this, context);
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
