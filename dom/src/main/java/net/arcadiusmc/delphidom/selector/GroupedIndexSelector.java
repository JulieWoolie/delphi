package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;

public record GroupedIndexSelector(AnB anb, SelectorGroup group) implements IndexSelector {

  @Override
  public boolean test(boolean inverted, DelphiElement root, DelphiElement el) {
    IndexResult gIndex = IndexResult.indexMatching(inverted, el, e -> group.test(root, e));

    if (gIndex.indexOrBefore() == -1) {
      return false;
    }

    return anb.indexMatches(gIndex.indexOrBefore());
  }

  @Override
  public void append(StringBuilder builder) {
    anb.append(builder);
    builder.append(" of ");
    group.append(builder);
  }
}
