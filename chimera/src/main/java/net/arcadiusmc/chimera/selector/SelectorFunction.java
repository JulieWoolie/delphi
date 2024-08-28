package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;

public interface SelectorFunction {

  SelectorFunction MATCH_ALL = MatchAll.MATCH_ALL;

  boolean test(Element root, Element element);

  void append(StringBuilder builder);

  void appendSpec(Spec spec);
}
