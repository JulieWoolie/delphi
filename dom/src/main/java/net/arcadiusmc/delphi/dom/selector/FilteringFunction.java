package net.arcadiusmc.delphi.dom.selector;

import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;

public interface FilteringFunction extends SelectorFunction {

  @Override
  default List<DelphiElement> selectNext(List<DelphiElement> elements) {
    elements.removeIf(element -> !test(element));
    return elements;
  }
}
