package com.juliewoolie.chimera.selector;


import com.juliewoolie.dom.Element;

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
