package net.arcadiusmc.chimera.selector;


import net.arcadiusmc.dom.Element;

public record GroupedIndexSelector(AnB anb, Selector group) implements IndexSelector {

  @Override
  public boolean test(boolean inverted, Element root, Element el) {
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
