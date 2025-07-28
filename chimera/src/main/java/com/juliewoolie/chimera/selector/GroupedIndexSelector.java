package com.juliewoolie.chimera.selector;


import com.juliewoolie.dom.Element;

public record GroupedIndexSelector(AnB anb, Selector group) implements IndexSelector {

  @Override
  public boolean test(boolean inverted, Element el) {
    IndexResult gIndex = IndexResult.indexMatching(inverted, el, group);

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
