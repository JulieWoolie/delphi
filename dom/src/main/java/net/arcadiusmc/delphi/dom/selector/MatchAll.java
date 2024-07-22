package net.arcadiusmc.delphi.dom.selector;

import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.delphi.dom.DelphiNode;

final class MatchAll implements SelectorFunction {

  @Override
  public boolean test(DelphiElement element) {
    return true;
  }

  @Override
  public List<DelphiElement> selectNext(DelphiElement element) {
    List<DelphiElement> elements = new ArrayList<>(element.childList().size());

    for (DelphiNode delphiNode : element.childList()) {
      if (!(delphiNode instanceof DelphiElement el)) {
        continue;
      }
      elements.add(el);
    }

    return elements;
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append("*");
  }

  @Override
  public void appendSpec(Spec spec) {

  }
}
