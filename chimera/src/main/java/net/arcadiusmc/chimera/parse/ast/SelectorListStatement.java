package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorList;

@Getter @Setter
public class SelectorListStatement extends SelectorExpression {

  private final List<RegularSelectorStatement> selectors = new ArrayList<>();

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.selectorGroup(this, context);
  }

  @Override
  public Selector compile(CompilerErrors errors) {
    if (this.selectors.isEmpty()) {
      return Selector.MATCH_ALL;
    }
    if (selectors.size() == 1) {
      return selectors.getFirst().compile(errors);
    }

    SelectorList list = new SelectorList(this.selectors.size());

    for (int i = 0; i < selectors.size(); i++) {
      list.add(selectors.get(i).compile(errors));
    }

    return list;
  }
}
