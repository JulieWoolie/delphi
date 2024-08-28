package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;

enum MatchAll implements SelectorFunction {
  MATCH_ALL,
  ;

  @Override
  public boolean test(Element root, Element element) {
    return true;
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append('*');
  }

  @Override
  public void appendSpec(Spec spec) {

  }
}
