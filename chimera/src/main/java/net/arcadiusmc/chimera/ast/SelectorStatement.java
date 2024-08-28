package net.arcadiusmc.chimera.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.CompilerErrors;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorNode;

@Getter @Setter
public class SelectorStatement extends Node {

  private final List<SelectorNodeStatement> nodes = new ArrayList<>();

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.selector(this, context);
  }

  public Selector compile(CompilerErrors errors) {
    SelectorNode[] nodes = new SelectorNode[this.nodes.size()];
    for (int i = 0; i < this.nodes.size(); i++) {
      nodes[i] = this.nodes.get(i).compile(errors);
    }
    return new Selector(nodes);
  }
}
