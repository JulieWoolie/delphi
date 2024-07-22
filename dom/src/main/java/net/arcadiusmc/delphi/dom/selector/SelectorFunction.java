package net.arcadiusmc.delphi.dom.selector;

import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;

public interface SelectorFunction {

  SelectorFunction ALL = new MatchAll();

  boolean test(DelphiElement element);

  List<DelphiElement> selectNext(DelphiElement element);

  void append(StringBuilder builder);

  void appendSpec(Spec spec);
}
