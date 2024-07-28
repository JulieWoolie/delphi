package net.arcadiusmc.delphidom.selector;

import java.util.List;
import net.arcadiusmc.delphidom.DelphiElement;

public interface SelectorFunction {

  SelectorFunction ALL = new MatchAll();

  boolean test(DelphiElement element);

  List<DelphiElement> selectNext(List<DelphiElement> elements);

  void append(StringBuilder builder);

  void appendDebug(StringBuilder builder);

  void appendSpec(Spec spec);
}
