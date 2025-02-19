package net.arcadiusmc.chimera.selector;


import net.arcadiusmc.dom.Element;

public record SimpleIndexSelector(AnB anb) implements IndexSelector {

  @Override
  public boolean test(boolean inverted, Element el) {
    int idx = IndexSelector.getIndex(el, inverted);
    return anb.indexMatches(idx);
  }

  @Override
  public void append(StringBuilder builder) {
    anb.append(builder);
  }
}
