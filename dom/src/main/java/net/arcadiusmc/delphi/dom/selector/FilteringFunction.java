package net.arcadiusmc.delphi.dom.selector;

import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.delphi.dom.DelphiNode;

public interface FilteringFunction extends SelectorFunction {

  @Override
  default List<DelphiElement> selectNext(DelphiElement element) {
    List<DelphiElement> nodes = new ArrayList<>();

    for (DelphiNode delphiNode : element.childList()) {
      if (!(delphiNode instanceof DelphiElement el)) {
        continue;
      }

      if (!test(el)) {
        continue;
      }

      nodes.add(el);
    }

    return nodes;
  }
}
