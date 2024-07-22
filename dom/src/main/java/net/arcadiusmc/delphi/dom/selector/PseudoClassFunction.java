package net.arcadiusmc.delphi.dom.selector;

import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;

public record PseudoClassFunction(PseudoClass pseudo) implements SelectorFunction {

  @Override
  public boolean test(DelphiElement element) {
    return false;
  }

  @Override
  public List<DelphiElement> selectNext(DelphiElement element) {
    return List.of();
  }

  @Override
  public void append(StringBuilder builder) {

  }

  @Override
  public void appendSpec(Spec spec) {

  }
}
