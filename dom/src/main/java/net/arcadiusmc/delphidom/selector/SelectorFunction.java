package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;

public interface SelectorFunction {

  SelectorFunction ALL = new MatchAll();

  boolean test(DelphiElement root, DelphiElement element);

  void append(StringBuilder builder);

  void appendDebug(StringBuilder builder);

  void appendSpec(Spec spec);
}
