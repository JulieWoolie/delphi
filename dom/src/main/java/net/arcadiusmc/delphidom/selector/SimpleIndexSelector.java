package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;

public record SimpleIndexSelector(AnB anb) implements IndexSelector {

  @Override
  public boolean test(boolean inverted, DelphiElement root, DelphiElement el) {
    int idx = IndexSelector.getIndex(el, inverted);
    return anb.indexMatches(idx);
  }

  @Override
  public void append(StringBuilder builder) {
    anb.append(builder);
  }
}
