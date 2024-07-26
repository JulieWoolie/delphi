package net.arcadiusmc.delphi.dom.selector;

import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;

public interface SelectorFunction {

  SelectorFunction ALL = new MatchAll();

  boolean test(DelphiElement element);

  List<DelphiElement> selectNext(List<DelphiElement> elements);

  void append(StringBuilder builder);

  void appendDebug(StringBuilder builder);

  void appendSpec(Spec spec);
}
