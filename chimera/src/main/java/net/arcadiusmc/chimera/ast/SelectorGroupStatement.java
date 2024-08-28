package net.arcadiusmc.chimera.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.CompilerErrors;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorGroup;

@Getter @Setter
public class SelectorGroupStatement extends Node {

  private final List<SelectorStatement> selectors = new ArrayList<>();

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.selectorGroup(this, context);
  }

  public SelectorGroup compile(CompilerErrors errors) {
    Selector[] selectors = new Selector[this.selectors.size()];

    for (int i = 0; i < this.selectors.size(); i++) {
      selectors[i] = this.selectors.get(i).compile(errors);
    }

    return new SelectorGroup(selectors);
  }
}
