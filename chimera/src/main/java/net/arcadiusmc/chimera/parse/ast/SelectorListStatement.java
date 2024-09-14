package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorList;
import net.arcadiusmc.chimera.selector.SelectorList.ListStyle;
import net.arcadiusmc.chimera.selector.SelectorList.ListType;

@Getter @Setter
public class SelectorListStatement extends SelectorExpression {

  private final List<RegularSelectorStatement> selectors = new ArrayList<>();

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.selectorGroup(this);
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
    list.setType(ListType.OR);
    list.setStyle(ListStyle.COMMA_LIST);

    for (int i = 0; i < selectors.size(); i++) {
      list.add(selectors.get(i).compile(errors));
    }

    return list;
  }
}
